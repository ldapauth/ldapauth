package com.ldapauth.autoconfigure;

import com.ldapauth.crypto.keystore.KeyStoreLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@AutoConfiguration
public class KeyStoreAutoConfiguration  implements InitializingBean {
    private static final  Logger _logger =
            LoggerFactory.getLogger(KeyStoreAutoConfiguration.class);

    /**
     * keyStoreLoader .
     * @return
     */
    @Bean
    public KeyStoreLoader keyStoreLoader(
            @Value("${ldapauth.saml.v20.idp.issuing.entity.id}") String entityName,
            @Value("${ldapauth.saml.v20.idp.keystore.password}") String keystorePassword,
            @Value("${ldapauth.saml.v20.idp.keystore}") Resource keystoreFile) {
    	_logger.debug("entityName {} " , entityName);
        KeyStoreLoader keyStoreLoader = new KeyStoreLoader();
        keyStoreLoader.setEntityName(entityName);
        keyStoreLoader.setKeystorePassword(keystorePassword);
        keyStoreLoader.setKeystoreFile(keystoreFile);
        return keyStoreLoader;
    }

    /**
     * spKeyStoreLoader .
     * @return
     */
    @Bean
    public KeyStoreLoader spKeyStoreLoader(
            @Value("${ldapauth.saml.v20.sp.issuing.entity.id}") String entityName,
            @Value("${ldapauth.saml.v20.sp.keystore.password}") String keystorePassword,
            @Value("${ldapauth.saml.v20.sp.keystore}") Resource keystoreFile) {
        KeyStoreLoader keyStoreLoader = new KeyStoreLoader();
        keyStoreLoader.setEntityName(entityName);
        keyStoreLoader.setKeystorePassword(keystorePassword);
        keyStoreLoader.setKeystoreFile(keystoreFile);
        return keyStoreLoader;
    }

    /**
     * spKeyStoreLoader .
     * @return
     */
    @Bean
    public String spIssuingEntityName(
            @Value("${ldapauth.saml.v20.sp.issuing.entity.id}") String spIssuingEntityName) {
    	_logger.debug("service provider Issuing EntityName {} " , spIssuingEntityName);
        return spIssuingEntityName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
