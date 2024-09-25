package com.ldapauth.authz.oauth2.provider.code;

import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;
import com.ldapauth.cache.CacheService;
import com.ldapauth.pojo.entity.apps.details.ClientAppsOIDCDetails;
import com.ldapauth.util.ObjectTransformer;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of authorization code services that stores the codes and authentication in Redis.
 *
 * @author Crystal.Sea
 */
public class PgAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

	CacheService cacheService;

	public static String PREFIX = "LDAPAUTH_OAUTH_V20_CODE_";

	protected int codeValiditySeconds = 60 * 10; //default 10 minutes.

	ClientAppsOIDCDetails details = null;

	public void setDetails(ClientAppsOIDCDetails details) {
		this.details = details;
	}

	/**
	 */
	public PgAuthorizationCodeServices(CacheService cacheService) {
		super();
		this.cacheService = cacheService;
	}

	@Override
	protected void store(String code, OAuth2Authentication authentication) {

		String value = ObjectTransformer.serialize(authentication);
		int sdcodeValiditySeconds = codeValiditySeconds;

		if (Objects.nonNull(details)) {
			sdcodeValiditySeconds = details.getCodeValidity().intValue();
		}
		cacheService.setCacheObject(prefixCode(code), value, sdcodeValiditySeconds,TimeUnit.SECONDS);

	}

	@Override
	public OAuth2Authentication remove(String code) {
		String value = cacheService.getCacheObject(prefixCode(code));
		if (StringUtils.isNotEmpty(value)) {
			OAuth2Authentication auth = ObjectTransformer.deserialize(value);
			cacheService.deleteObject(prefixCode(code));
			return auth;
		}
		return null;
	}

	private String prefixCode(String code){
		return PREFIX + code;
	}

}
