package com.ldapauth.autoconfigure;

import com.ldapauth.authz.oauth2.provider.client.ClientDetailsUserDetailsService;
import com.ldapauth.authz.oauth2.provider.client.RestClientDetailsService;
import com.ldapauth.persistence.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * like RestClientAutoConfiguration for mgmt
 * @author Crystal.Sea
 *
 */
@AutoConfiguration
public class RestClientAutoConfiguration {
    private static final  Logger logger = LoggerFactory.getLogger(RestClientAutoConfiguration.class);


    /**
     * ProviderManager.
     * @return oauth20ClientAuthenticationManager
     */
    @Bean(name = "restProviderManager")
    public ProviderManager restProviderManager(
            ClientService appsService,
            @Qualifier("passwordReciprocal")
            PasswordEncoder passwordReciprocal
            ) {
        RestClientDetailsService restClientDetailsService = new RestClientDetailsService(appsService);
        ClientDetailsUserDetailsService cientDetailsUserDetailsService =
                new ClientDetailsUserDetailsService(restClientDetailsService);
        DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordReciprocal);
        daoAuthenticationProvider.setUserDetailsService(cientDetailsUserDetailsService);
        ProviderManager authenticationManager = new ProviderManager(daoAuthenticationProvider);
        logger.debug("Client Authentication Manager init.");
        return authenticationManager;
    }

}
