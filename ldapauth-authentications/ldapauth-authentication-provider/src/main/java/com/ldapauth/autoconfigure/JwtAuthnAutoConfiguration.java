package com.ldapauth.autoconfigure;

import com.ldapauth.authn.support.jwt.JwtLoginService;
import com.ldapauth.crypto.jose.keystore.JWKSetKeyStore;
import com.nimbusds.jose.JOSEException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.ldapauth.crypto.jwt.signer.service.impl.DefaultJwtSigningAndValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

/**
 * JWT登录JWKS配置，签名和验证自动配置
 *
 * @author Crystal.Sea
 *
 */
@AutoConfiguration
public class JwtAuthnAutoConfiguration implements InitializingBean {
    private static final  Logger _logger = LoggerFactory.getLogger(JwtAuthnAutoConfiguration.class);

    /**
     * jwt Login JwkSetKeyStore.
     * @return
     */
    @Bean
    public JWKSetKeyStore jwtLoginJwkSetKeyStore() {
        JWKSetKeyStore jwkSetKeyStore = new JWKSetKeyStore();
        ClassPathResource classPathResource = new ClassPathResource("/config/loginjwkkeystore.jwks");
        jwkSetKeyStore.setLocation(classPathResource);
        _logger.debug("JWT Login JwkSet KeyStore init.");
        return jwkSetKeyStore;
    }

    /**
     * jwt Login ValidationService.
     * @return
     * @throws JOSEException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    @Bean
    public DefaultJwtSigningAndValidationService jwtLoginValidationService(
            JWKSetKeyStore jwtLoginJwkSetKeyStore)
                    throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        DefaultJwtSigningAndValidationService jwtSignerValidationService =
                new DefaultJwtSigningAndValidationService(jwtLoginJwkSetKeyStore);
        jwtSignerValidationService.setDefaultSignerKeyId("ldapauth_rsa");
        jwtSignerValidationService.setDefaultSigningAlgorithmName("RS256");
        _logger.debug("JWT Login Signing and Validation init.");
        return jwtSignerValidationService;
    }

    /**
     * Jwt LoginService.
     * @return
     */
    @Bean
    public JwtLoginService jwtLoginService(
            @Value("${ldapauth.login.jwt.issuer}")
            String issuer,
            DefaultJwtSigningAndValidationService jwtLoginValidationService) {
        JwtLoginService jwtLoginService = new JwtLoginService(
                    jwtLoginValidationService,
                    issuer
                );
        _logger.debug("JWT Login Service init.");
        return jwtLoginService;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
