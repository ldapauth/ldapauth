package com.ldapauth.authz.oauth2.provider.token.store;

import com.ldapauth.cache.CacheService;

public class TokenStoreFactory {


	 public PgTokenStore getTokenStore(CacheService cacheService) {
	        return new PgTokenStore(cacheService);
	    }
}
