package com.ldapauth.autoconfigure;

import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.authn.provider.AbstractAuthenticationProvider;
import com.ldapauth.authn.realm.AbstractAuthenticationRealm;
import com.ldapauth.authn.realm.ldap.LdapAuthenticationRealm;
import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.persistence.repository.LoginRepository;
import com.ldapauth.authn.provider.AuthenticationProviderFactory;

import com.ldapauth.authn.provider.impl.NormalAuthenticationProvider;
import com.ldapauth.authn.provider.impl.TrustedAuthenticationProvider;
import com.ldapauth.authn.support.rememberme.AbstractRemeberMeManager;
import com.ldapauth.authn.support.rememberme.JdbcRemeberMeManager;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.persistence.service.LoginLogService;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.persistence.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 认证提供者自动配置，可根据需要增加新的提供者
 *
 * @author Crystal.Sea
 *
 */
@AutoConfiguration
public class AuthnProviderAutoConfiguration  implements InitializingBean {
    private static final  Logger _logger =
            LoggerFactory.getLogger(AuthnProviderAutoConfiguration.class);

    @Bean
    public AbstractAuthenticationProvider authenticationProvider(
    		AbstractAuthenticationProvider normalAuthenticationProvider,
    		AbstractAuthenticationProvider trustedAuthenticationProvider
    		) {
    	AuthenticationProviderFactory authenticationProvider = new AuthenticationProviderFactory();
    	//常规提供者
    	authenticationProvider.addAuthenticationProvider(normalAuthenticationProvider);
    	//信任提供者
    	authenticationProvider.addAuthenticationProvider(trustedAuthenticationProvider);

    	return authenticationProvider;
    }

    @Bean
    public AbstractAuthenticationProvider normalAuthenticationProvider(
    		AbstractAuthenticationRealm authenticationRealm,
    		ApplicationConfig applicationConfig,
    	    SessionManager sessionManager,
    	    AuthTokenService authTokenService
    		) {
    	_logger.debug("init Normal authentication Provider .");
    	return new NormalAuthenticationProvider(
        		authenticationRealm,
        		applicationConfig,
        		sessionManager,
        		authTokenService
        	);
    }


    @Bean
    public AbstractAuthenticationProvider trustedAuthenticationProvider(
    		AbstractAuthenticationRealm authenticationRealm,
    		ApplicationConfig applicationConfig,
    	    SessionManager sessionManager
    		) {
    	_logger.debug("init Trusted authentication Provider .");
    	return new TrustedAuthenticationProvider(
        		authenticationRealm,
        		applicationConfig,
        		sessionManager
        	);
    }


    @Bean
    public LoginRepository loginRepository(LoginLogService loginLogService,UserInfoService userInfoService) {
        return new LoginRepository(loginLogService,userInfoService);
    }


    /**
     * remeberMeService .
     * @return
     */
    @Bean
    public AbstractRemeberMeManager remeberMeManager(
            @Value("${ldapauth.server.persistence}") int persistence,
            @Value("${ldapauth.login.remeberme.validity}") int validity,
            ApplicationConfig applicationConfig,
            AuthTokenService authTokenService,
            JdbcTemplate jdbcTemplate) {
    	_logger.trace("init RemeberMeManager , validity {}." , validity);
        return new  JdbcRemeberMeManager(
        		jdbcTemplate,applicationConfig,authTokenService,validity);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
