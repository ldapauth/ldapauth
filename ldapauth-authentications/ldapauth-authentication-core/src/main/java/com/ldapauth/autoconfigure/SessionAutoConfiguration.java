package com.ldapauth.autoconfigure;

import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.authn.session.SessionManagerFactory;
import com.ldapauth.authn.web.HttpSessionListenerAdapter;
import com.ldapauth.authn.web.SavedRequestAwareAuthenticationSuccessHandler;
import com.ldapauth.cache.CacheService;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.configuration.AuthJwkConfig;
import com.ldapauth.pojo.entity.PolicyLogin;
import com.ldapauth.persistence.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 会话自动配置
 *
 * @author Crystal.Sea
 *
 */
@AutoConfiguration
public class SessionAutoConfiguration  implements InitializingBean {
    private static final  Logger _logger = LoggerFactory.getLogger(SessionAutoConfiguration.class);

    @Bean(name = "savedRequestSuccessHandler")
    public SavedRequestAwareAuthenticationSuccessHandler
            savedRequestAwareAuthenticationSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler();
    }

    @Bean(name = "sessionManager")
    public SessionManager sessionManager(
            CacheService cacheService,
            AuthJwkConfig authJwkConfig
            ) {
        return new SessionManagerFactory(cacheService,authJwkConfig);
    }

    @Bean
    public HttpSessionListenerAdapter httpSessionListenerAdapter() {
        return new HttpSessionListenerAdapter();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
