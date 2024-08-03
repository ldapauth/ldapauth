package com.ldapauth.autoconfigure;

import com.ldapauth.cache.CacheService;
import com.ldapauth.pojo.entity.PolicyLogin;
import com.ldapauth.persistence.repository.PolicyRepository;
import com.ldapauth.authn.jwt.AuthRefreshTokenService;
import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.authn.jwt.CongressService;
import com.ldapauth.authn.jwt.InMemoryCongressService;
import com.ldapauth.authn.jwt.RedisCongressService;
import com.ldapauth.configuration.AuthJwkConfig;
import com.ldapauth.constants.ConstsPersistence;
import com.ldapauth.cache.MomentaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.nimbusds.jose.JOSEException;

/**
 * Token令牌自动配置
 *
 * @author Crystal.Sea
 *
 */
@AutoConfiguration
public class TokenAutoConfiguration  implements InitializingBean {
    private static final  Logger _logger =
            LoggerFactory.getLogger(TokenAutoConfiguration.class);

    /**
     * 认证令牌
     * @param authJwkConfig
     * @param redisConnFactory
     * @param momentaryService
     * @param refreshTokenService
     * @param persistence
     * @return
     * @throws JOSEException
     */
    @Bean
    public AuthTokenService authTokenService(
    		AuthJwkConfig authJwkConfig,
			CacheService cacheService,
    		MomentaryService  momentaryService,
    		AuthRefreshTokenService refreshTokenService,
    		PolicyRepository policyRepository,
    		@Value("${ldapauth.server.persistence}") int persistence) throws JOSEException {
		PolicyLogin loginPolicy = policyRepository.getPolicyLogin();
    	if(policyRepository != null) {
    		authJwkConfig.setExpires(loginPolicy.getAccessTokenValidity());
    		authJwkConfig.setRefreshExpires(loginPolicy.getRefreshTokenValidity());
    	}
    	CongressService congressService;
    	if (persistence == ConstsPersistence.REDIS) {
    		congressService = new RedisCongressService();
    		_logger.debug("RedisCongressService");
    	}else {
    		congressService = new InMemoryCongressService();
    		_logger.debug("InMemoryCongressService");
    	}

    	AuthTokenService authTokenService =
    				new AuthTokenService(
    							authJwkConfig,
								cacheService,
    							congressService,
    							momentaryService,
    							refreshTokenService
    						);

    	return authTokenService;
    }

    /**
     * 刷新令牌
     * @param authJwkConfig
     * @return
     * @throws JOSEException
     */
    @Bean
    public AuthRefreshTokenService refreshTokenService(AuthJwkConfig authJwkConfig, PolicyRepository policyRepository) throws JOSEException {
		PolicyLogin loginPolicy = policyRepository.getPolicyLogin();
		if(policyRepository != null) {
			authJwkConfig.setExpires(loginPolicy.getAccessTokenValidity());
			authJwkConfig.setRefreshExpires(loginPolicy.getRefreshTokenValidity());
		}
    	return new AuthRefreshTokenService(authJwkConfig);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
