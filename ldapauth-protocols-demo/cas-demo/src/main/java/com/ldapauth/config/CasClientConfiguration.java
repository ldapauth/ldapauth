package com.ldapauth.config;

import lombok.var;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuration class providing default CAS client infrastructure filters.
 * This configuration facility is typically imported into Spring's Application Context via
 * {@link EnableCasClient} meta annotation.
 *
 * @author Dmitriy Kopylenko
 * @since 3.6.0
 */
@Configuration
@EnableConfigurationProperties(CasClientConfigurationProperties.class)
public class CasClientConfiguration {

    @Autowired
    CasClientConfigurationProperties configProps;

    private CasClientConfigurer casClientConfigurer;

    private static Map<String, String> constructInitParams(final String casUrlParamName, final String casUrlParamVal, final String clientHostUrlVal) {
        final Map<String, String> initParams = new HashMap<>(2);
        initParams.put(casUrlParamName, casUrlParamVal);
        initParams.put("serverName", clientHostUrlVal);
        return initParams;
    }

    private static void initFilter(final FilterRegistrationBean filterRegistrationBean,
                                   final Filter targetFilter,
                                   final int filterOrder,
                                   final Map<String, String> initParams,
                                   final Collection<String> urlPatterns) {

        filterRegistrationBean.setFilter(targetFilter);
        filterRegistrationBean.setOrder(filterOrder);
        filterRegistrationBean.setInitParameters(initParams);
        if (!urlPatterns.isEmpty()) {
            filterRegistrationBean.setUrlPatterns(urlPatterns);
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "cas", name = "skipTicketValidation", havingValue = "false", matchIfMissing = true)
    public FilterRegistrationBean casValidationFilter() {
        final var validationFilter = new FilterRegistrationBean();
        final Filter targetCasValidationFilter;
        switch (this.configProps.getValidationType()) {
            case CAS:
                targetCasValidationFilter = new Cas20ProxyReceivingTicketValidationFilter();
                break;
            case CAS3:
            default:
                targetCasValidationFilter = new Cas30ProxyReceivingTicketValidationFilter();
                break;
        }

        initFilter(validationFilter,
            targetCasValidationFilter,
            1,
            constructInitParams(ConfigurationKeys.CAS_SERVER_URL_PREFIX.getName(), this.configProps.getServerUrlPrefix(), this.configProps.getClientHostUrl()),
            this.configProps.getValidationUrlPatterns());

        if (this.configProps.getUseSession() != null) {
            validationFilter.getInitParameters().put(ConfigurationKeys.USE_SESSION.getName(), String.valueOf(this.configProps.getUseSession()));
        }
        if (this.configProps.getRedirectAfterValidation() != null) {
            validationFilter.getInitParameters().put(ConfigurationKeys.REDIRECT_AFTER_VALIDATION.getName(),
                String.valueOf(this.configProps.getRedirectAfterValidation()));
        }

        if (this.configProps.getHostnameVerifier() != null) {
            validationFilter.getInitParameters().put(ConfigurationKeys.HOSTNAME_VERIFIER.getName(), this.configProps.getHostnameVerifier());
        }
        if (this.configProps.getSslConfigFile() != null) {
            validationFilter.getInitParameters().put(ConfigurationKeys.SSL_CONFIG_FILE.getName(), this.configProps.getSslConfigFile());
        }

        //Proxy tickets validation
        if (this.configProps.getAcceptAnyProxy() != null) {
            validationFilter.getInitParameters().put(ConfigurationKeys.ACCEPT_ANY_PROXY.getName(), String.valueOf(this.configProps.getAcceptAnyProxy()));
        }
        if (!this.configProps.getAllowedProxyChains().isEmpty()) {
            validationFilter.getInitParameters().put(ConfigurationKeys.ALLOWED_PROXY_CHAINS.getName(),
                StringUtils.collectionToDelimitedString(this.configProps.getAllowedProxyChains(), " "));
        }
        if (this.configProps.getProxyCallbackUrl() != null) {
            validationFilter.getInitParameters().put(ConfigurationKeys.PROXY_CALLBACK_URL.getName(), this.configProps.getProxyCallbackUrl());
        }
        if (this.configProps.getProxyReceptorUrl() != null) {
            validationFilter.getInitParameters().put(ConfigurationKeys.PROXY_RECEPTOR_URL.getName(), this.configProps.getProxyReceptorUrl());
        }

        if (this.casClientConfigurer != null) {
            this.casClientConfigurer.configureValidationFilter(validationFilter);
        }
        return validationFilter;
    }

    @Bean
    public FilterRegistrationBean casAuthenticationFilter() {
        final var authnFilter = new FilterRegistrationBean();
        final Filter targetCasAuthnFilter =
            this.configProps.getValidationType() == EnableCasClient.ValidationType.CAS
            || configProps.getValidationType() == EnableCasClient.ValidationType.CAS3
                ? new AuthenticationFilter()
                : new AuthenticationFilter();

        initFilter(authnFilter,
            targetCasAuthnFilter,
            2,
            constructInitParams(ConfigurationKeys.CAS_SERVER_LOGIN_URL.getName(), this.configProps.getServerLoginUrl(), this.configProps.getClientHostUrl()),
            this.configProps.getAuthenticationUrlPatterns());

        if (this.configProps.getGateway() != null) {
            authnFilter.getInitParameters().put(ConfigurationKeys.GATEWAY.getName(), String.valueOf(this.configProps.getGateway()));
        }

        if (this.casClientConfigurer != null) {
            this.casClientConfigurer.configureAuthenticationFilter(authnFilter);
        }
        return authnFilter;
    }

    @Bean
    public FilterRegistrationBean casHttpServletRequestWrapperFilter() {
        final var reqWrapperFilter = new FilterRegistrationBean();
        reqWrapperFilter.setFilter(new HttpServletRequestWrapperFilter());
        if (!this.configProps.getRequestWrapperUrlPatterns().isEmpty()) {
            reqWrapperFilter.setUrlPatterns(this.configProps.getRequestWrapperUrlPatterns());
        }
        reqWrapperFilter.setOrder(3);

        if (this.casClientConfigurer != null) {
            this.casClientConfigurer.configureHttpServletRequestWrapperFilter(reqWrapperFilter);
        }
        return reqWrapperFilter;
    }

    @Bean
    public FilterRegistrationBean casAssertionThreadLocalFilter() {
        final var assertionTLFilter = new FilterRegistrationBean();
        assertionTLFilter.setFilter(new AssertionThreadLocalFilter());
        if (!this.configProps.getAssertionThreadLocalUrlPatterns().isEmpty()) {
            assertionTLFilter.setUrlPatterns(this.configProps.getAssertionThreadLocalUrlPatterns());
        }
        assertionTLFilter.setOrder(4);

        if (this.casClientConfigurer != null) {
            this.casClientConfigurer.configureAssertionThreadLocalFilter(assertionTLFilter);
        }
        return assertionTLFilter;
    }

    @Autowired(required = false)
    void setConfigurers(final Collection<CasClientConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException(configurers.size() + " implementations of " +
                                            "CasClientConfigurer were found when only 1 was expected. " +
                                            "Refactor the configuration such that CasClientConfigurer is " +
                                            "implemented only once or not at all.");
        }
        this.casClientConfigurer = configurers.iterator().next();
    }

    @Bean
    @ConditionalOnProperty(prefix = "cas", value = "single-logout.enabled", havingValue = "true")
    public FilterRegistrationBean casSingleSignOutFilter() {
        final var singleSignOutFilter = new FilterRegistrationBean();
        singleSignOutFilter.setFilter(new SingleSignOutFilter());
        final Map<String, String> initParameters = new HashMap<>(1);
        initParameters.put(ConfigurationKeys.CAS_SERVER_URL_PREFIX.getName(), configProps.getServerUrlPrefix());
        singleSignOutFilter.setInitParameters(initParameters);
        singleSignOutFilter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return singleSignOutFilter;
    }

    @Bean
    @ConditionalOnProperty(prefix = "cas", value = "single-logout.enabled", havingValue = "true")
    public ServletListenerRegistrationBean<EventListener> casSingleSignOutListener() {
        final var singleSignOutListener = new ServletListenerRegistrationBean<>();
        singleSignOutListener.setListener(new SingleSignOutHttpSessionListener());
        singleSignOutListener.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return singleSignOutListener;
    }

}
