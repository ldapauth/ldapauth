package com.ldapauth.autoconfigure;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.servlet.Filter;
import com.ldapauth.authz.oauth2.common.OAuth2Constants;
import com.ldapauth.authz.oauth2.provider.ClientDetailsService;
import com.ldapauth.authz.oauth2.provider.OAuth2UserDetailsService;
import com.ldapauth.authz.oauth2.provider.client.ClientDetailsUserDetailsService;
import com.ldapauth.authz.oauth2.provider.client.JdbcClientDetailsService;
import com.ldapauth.authz.oauth2.provider.code.AuthorizationCodeServices;
import com.ldapauth.authz.oauth2.provider.code.AuthorizationCodeServicesFactory;
import com.ldapauth.authz.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import com.ldapauth.authz.oauth2.provider.request.DefaultOAuth2RequestFactory;
import com.ldapauth.authz.oauth2.provider.token.TokenStore;
import com.ldapauth.authz.oauth2.provider.token.DefaultTokenServices;
import com.ldapauth.authz.oauth2.provider.token.store.JwtAccessTokenConverter;
import com.ldapauth.authz.oauth2.provider.token.store.TokenStoreFactory;
import com.ldapauth.authz.oidc.idtoken.OIDCIdTokenEnhancer;
import com.ldapauth.cache.CacheService;
import com.ldapauth.configuration.oidc.OIDCProviderMetadataDetails;
import com.ldapauth.crypto.jose.keystore.JWKSetKeyStore;
import com.ldapauth.crypto.jwt.encryption.service.impl.DefaultJwtEncryptionAndDecryptionService;
import com.ldapauth.crypto.jwt.signer.service.impl.DefaultJwtSigningAndValidationService;
import com.ldapauth.persistence.repository.LoginRepository;
import com.ldapauth.persistence.service.ClientAppsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;

@AutoConfiguration
@ComponentScan(basePackages = {
        "com.ldapauth.authz.oauth2.provider.endpoint",
        "com.ldapauth.authz.oauth2.provider.userinfo.endpoint",
        "com.ldapauth.authz.oauth2.provider.wellknown.endpoint"
})
public class Oauth20AutoConfiguration implements InitializingBean {
    private static final  Logger _logger = LoggerFactory.getLogger(Oauth20AutoConfiguration.class);

    @Bean
    public FilterRegistrationBean<Filter> TokenEndpointAuthenticationFilter() {
        _logger.debug("TokenEndpointAuthenticationFilter init ");
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
        registration.setFilter(new TokenEndpointAuthenticationFilter());
        registration.addUrlPatterns(
        							OAuth2Constants.ENDPOINT.ENDPOINT_TOKEN + "/*",
        							OAuth2Constants.ENDPOINT.ENDPOINT_TENCENT_IOA_TOKEN + "/*");
        registration.setName("TokenEndpointAuthenticationFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * OIDCProviderMetadataDetails.
     * Self-issued Provider Metadata
     * http://openid.net/specs/openid-connect-core-1_0.html#SelfIssued
     */
    @Bean(name = "oidcProviderMetadata")
    public OIDCProviderMetadataDetails OIDCProviderMetadataDetails(
            @Value("${ldapauth.oidc.metadata.issuer}")
            String issuer,
            @Value("${ldapauth.oidc.metadata.authorizationEndpoint}")
            URI authorizationEndpoint,
            @Value("${ldapauth.oidc.metadata.tokenEndpoint}")
            URI tokenEndpoint,
            @Value("${ldapauth.oidc.metadata.userinfoEndpoint}")
            URI userinfoEndpoint) {
        _logger.debug("OIDC Provider Metadata Details init .");
        OIDCProviderMetadataDetails oidcProviderMetadata = new OIDCProviderMetadataDetails();
        oidcProviderMetadata.setIssuer(issuer);
        oidcProviderMetadata.setAuthorizationEndpoint(authorizationEndpoint);
        oidcProviderMetadata.setTokenEndpoint(tokenEndpoint);
        oidcProviderMetadata.setUserinfoEndpoint(userinfoEndpoint);
        return oidcProviderMetadata;
    }

    /**
     * jwtSetKeyStore.
     * @return
     */
    @Bean(name = "jwkSetKeyStore")
    public JWKSetKeyStore jwkSetKeyStore() {
        JWKSetKeyStore jwkSetKeyStore = new JWKSetKeyStore();
        ClassPathResource classPathResource = new ClassPathResource("/config/keystore.jwks");
        jwkSetKeyStore.setLocation(classPathResource);
        _logger.debug("JWKSet KeyStore init.");
        return jwkSetKeyStore;
    }

    /**
     * jwtSetKeyStore.
     * @return
     * @throws JOSEException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    @Bean(name = "jwtSignerValidationService")
    public DefaultJwtSigningAndValidationService jwtSignerValidationService(
            JWKSetKeyStore jwkSetKeyStore)
                    throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        DefaultJwtSigningAndValidationService jwtSignerValidationService =
                new DefaultJwtSigningAndValidationService(jwkSetKeyStore);
        jwtSignerValidationService.setDefaultSignerKeyId("maxkey_rsa");
        jwtSignerValidationService.setDefaultSigningAlgorithmName("RS256");
        _logger.debug("JWT Signer and Validation Service init.");
        return jwtSignerValidationService;
    }

    /**
     * jwtSetKeyStore.
     * @return
     * @throws JOSEException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    @Bean(name = "jwtEncryptionService")
    public DefaultJwtEncryptionAndDecryptionService jwtEncryptionService(
            JWKSetKeyStore jwkSetKeyStore)
                    throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        DefaultJwtEncryptionAndDecryptionService jwtEncryptionService =
                new DefaultJwtEncryptionAndDecryptionService(jwkSetKeyStore);
        jwtEncryptionService.setDefaultAlgorithm(JWEAlgorithm.RSA_OAEP_256);//RSA1_5
        jwtEncryptionService.setDefaultDecryptionKeyId("maxkey_rsa");
        jwtEncryptionService.setDefaultEncryptionKeyId("maxkey_rsa");
        _logger.debug("JWT Encryption and Decryption Service init.");
        return jwtEncryptionService;
    }

    /**
     * tokenEnhancer.
     * @return
     */
    @Bean(name = "tokenEnhancer")
    public OIDCIdTokenEnhancer tokenEnhancer(
            OIDCProviderMetadataDetails oidcProviderMetadata,
            ClientDetailsService oauth20JdbcClientDetailsService) {
        OIDCIdTokenEnhancer tokenEnhancer = new OIDCIdTokenEnhancer();
        tokenEnhancer.setClientDetailsService(oauth20JdbcClientDetailsService);
        tokenEnhancer.setProviderMetadata(oidcProviderMetadata);
        _logger.debug("OIDC IdToken Enhancer init.");
        return tokenEnhancer;
    }
    //以上部分为了支持OpenID Connect 1.0


    /**
     * AuthorizationCodeServices.
     * @return oauth20AuthorizationCodeServices
     */
    @Bean(name = "oauth20AuthorizationCodeServices")
    public AuthorizationCodeServices oauth20AuthorizationCodeServices(CacheService cacheService) {
        _logger.debug("OAuth 2 Authorization Code Services init.");
        return new AuthorizationCodeServicesFactory().getService(cacheService);
    }

    /**
     * TokenStore.
     * @return oauth20TokenStore
     */
    @Bean(name = "oauth20TokenStore")
    public TokenStore oauth20TokenStore(CacheService cacheService) {
        _logger.debug("OAuth 2 TokenStore init.");
        return new TokenStoreFactory().getTokenStore(cacheService);
    }

    /**
     * jwtAccessTokenConverter.
     * @return converter
     */
    @Bean(name = "converter")
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        _logger.debug("OAuth 2 Jwt AccessToken Converter init.");
        return jwtAccessTokenConverter;
    }

    /**
     * clientDetailsService.
     * @return oauth20JdbcClientDetailsService
     */
    @Bean(name = "oauth20JdbcClientDetailsService")
    public JdbcClientDetailsService jdbcClientDetailsService(ClientAppsService appsService) {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(appsService);
        //clientDetailsService.setPasswordEncoder(passwordReciprocal);
        _logger.debug("OAuth 2 ClientDetails Service init.");
        return clientDetailsService;
    }

    /**
     * clientDetailsUserDetailsService.
     * @return oauth20TokenServices
     */
    @Bean(name = "oauth20TokenServices")
    public DefaultTokenServices defaultTokenServices(
            JdbcClientDetailsService oauth20JdbcClientDetailsService,
            TokenStore oauth20TokenStore,
            OIDCIdTokenEnhancer tokenEnhancer) {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setClientDetailsService(oauth20JdbcClientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancer);
        tokenServices.setTokenStore(oauth20TokenStore);
        tokenServices.setSupportRefreshToken(true);
        _logger.debug("OAuth 2 Token Services init.");
        return tokenServices;
    }




    /**
     * OAuth2RequestFactory.
     * @return oAuth2RequestFactory
     */
    @Bean(name = "oAuth2RequestFactory")
    public DefaultOAuth2RequestFactory oauth2RequestFactory(
            JdbcClientDetailsService oauth20JdbcClientDetailsService) {
        DefaultOAuth2RequestFactory oauth2RequestFactory =
                new DefaultOAuth2RequestFactory(oauth20JdbcClientDetailsService);
        _logger.debug("OAuth 2 Request Factory init.");
        return oauth2RequestFactory;
    }

    /**
     * ProviderManager.
     * @return oauth20UserAuthenticationManager
     */
    @Bean(name = "oauth20UserAuthenticationManager")
    public ProviderManager oauth20UserAuthenticationManager(
            PasswordEncoder passwordEncoder,
            LoginRepository loginRepository
            ) {

        OAuth2UserDetailsService userDetailsService =new OAuth2UserDetailsService();
        userDetailsService.setLoginRepository(loginRepository);

        DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        ProviderManager authenticationManager = new ProviderManager(daoAuthenticationProvider);
        _logger.debug("OAuth 2 User Authentication Manager init.");
        return authenticationManager;
    }

    /**
     * ProviderManager.
     * @return oauth20ClientAuthenticationManager
     */
    @Bean(name = "oauth20ClientAuthenticationManager")
    public ProviderManager oauth20ClientAuthenticationManager(
            JdbcClientDetailsService oauth20JdbcClientDetailsService,
            PasswordEncoder passwordReciprocal
            ) {

        ClientDetailsUserDetailsService cientDetailsUserDetailsService =
                new ClientDetailsUserDetailsService(oauth20JdbcClientDetailsService);
        //cientDetailsUserDetailsService.setPasswordEncoder(passwordReciprocal);
        DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordReciprocal);
        daoAuthenticationProvider.setUserDetailsService(cientDetailsUserDetailsService);
        ProviderManager authenticationManager = new ProviderManager(daoAuthenticationProvider);
        _logger.debug("OAuth 2 Client Authentication Manager init.");
        return authenticationManager;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
