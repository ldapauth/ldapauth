package com.ldapauth.autoconfigure;

import java.util.Collections;
import javax.servlet.Filter;
import com.ldapauth.web.WebInstRequestFilter;
import com.ldapauth.web.WebXssRequestFilter;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.constants.ConstsTimeInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@AutoConfiguration
public class MvcAutoConfiguration implements InitializingBean , WebMvcConfigurer {
    private static final  Logger _logger = LoggerFactory.getLogger(MvcAutoConfiguration.class);


    /**
     * 消息处理，可以直接使用properties的key值，返回的是对应的value值
     * messageSource .
     * @return messageSource
     */
    @Bean (name = "messageSource")
    public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource(
            @Value("${spring.messages.basename:classpath:messages/message}")
            String messagesBasename)  {
        _logger.debug("Basename " + messagesBasename);
        String passwordPolicyMessagesBasename="classpath:messages/passwordpolicy_message";
        String globalMessagesBasename="classpath:messages/global_message";
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(messagesBasename,passwordPolicyMessagesBasename,globalMessagesBasename);
        messageSource.setUseCodeAsDefaultMessage(false);
        return messageSource;
    }

    /**
     * Locale Change Interceptor and Resolver definition .
     * @return localeChangeInterceptor
     */
    //@Primary
    @Bean (name = "localeChangeInterceptor")
    public LocaleChangeInterceptor localeChangeInterceptor()  {
        LocaleChangeInterceptor localeChangeInterceptor =
                new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }

    /**
     * upload file support .
     * @return multipartResolver
     */
    @Bean (name = "multipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver(
            @Value("${spring.servlet.multipart.max-file-size:0}") int maxUploadSize)  {
        _logger.debug("maxUploadSize " + maxUploadSize);
        CommonsMultipartResolver multipartResolver =
                new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(maxUploadSize);
        return multipartResolver;
    }

    /**
     * handlerMapping .
     * @return handlerMapping
     */
    @Bean (name = "handlerMapping")
    public RequestMappingHandlerMapping requestMappingHandlerMapping(
                                    LocaleChangeInterceptor localeChangeInterceptor) {
        RequestMappingHandlerMapping requestMappingHandlerMapping =
                new RequestMappingHandlerMapping();
        requestMappingHandlerMapping.setInterceptors(localeChangeInterceptor);
        return requestMappingHandlerMapping;
    }

    /**
     * cookieLocaleResolver .
     * @return cookieLocaleResolver
     */

    @Bean(name = "cookieLocaleResolver")
    public LocaleResolver cookieLocaleResolver(
            @Value("${ldapauth.server.domain:ldapauth.com}")
            String domainName
        ) {
        _logger.debug("DomainName " + domainName);
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setCookieName("ldap_locale");
        cookieLocaleResolver.setCookieDomain(domainName);
        cookieLocaleResolver.setCookieMaxAge(ConstsTimeInterval.TWO_WEEK);
        return cookieLocaleResolver;
    }

    /**
     * 配置默认错误页面（仅用于内嵌tomcat启动时） 使用这种方式，在打包为war后不起作用.
     *
     * @return webServerFactoryCustomizer
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
            @Override
            public void customize(ConfigurableWebServerFactory factory) {
                _logger.debug("WebServerFactoryCustomizer ... ");
                ErrorPage errorPage400 =
                        new ErrorPage(HttpStatus.BAD_REQUEST, "/exception/error/400");
                ErrorPage errorPage404 =
                        new ErrorPage(HttpStatus.NOT_FOUND, "/exception/error/404");
                ErrorPage errorPage500 =
                        new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/exception/error/500");
                factory.addErrorPages(errorPage400, errorPage404, errorPage500);
            }
        };
    }

    @Bean
    public SecurityContextHolderAwareRequestFilter securityContextHolderAwareRequestFilter() {
        _logger.debug("securityContextHolderAwareRequestFilter init ");
        return new SecurityContextHolderAwareRequestFilter();
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList(CorsConfiguration.ALL));
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        source.registerCorsConfiguration("/**", corsConfiguration);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>();
        bean.setOrder(1);
        bean.setFilter(new CorsFilter(source));
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    public FilterRegistrationBean<Filter> delegatingFilterProxy() {
        _logger.debug("delegatingFilterProxy init for /* ");
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<Filter>();
        registrationBean.setFilter(new DelegatingFilterProxy("securityContextHolderAwareRequestFilter"));
        registrationBean.addUrlPatterns("/*");
        //registrationBean.
        registrationBean.setName("delegatingFilterProxy");
        registrationBean.setOrder(2);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> webXssRequestFilter() {
        _logger.debug("webXssRequestFilter init for /* ");
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<Filter>(new WebXssRequestFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("webXssRequestFilter");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> WebInstRequestFilter(
    											ApplicationConfig applicationConfig) {
        _logger.debug("WebInstRequestFilter init for /* ");
        FilterRegistrationBean<Filter> registrationBean =
        		new FilterRegistrationBean<Filter>(new WebInstRequestFilter(applicationConfig));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("webInstRequestFilter");
        registrationBean.setOrder(4);
        return registrationBean;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
