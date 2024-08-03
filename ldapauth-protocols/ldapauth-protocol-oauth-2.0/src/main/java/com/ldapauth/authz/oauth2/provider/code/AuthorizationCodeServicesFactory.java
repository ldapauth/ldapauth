package com.ldapauth.authz.oauth2.provider.code;

import com.ldapauth.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationCodeServicesFactory {
	private static final  Logger _logger = LoggerFactory.getLogger(AuthorizationCodeServicesFactory.class);

	 public AuthorizationCodeServices getService(CacheService cacheService) {
	        return  new PgAuthorizationCodeServices(cacheService);
	    }
}
