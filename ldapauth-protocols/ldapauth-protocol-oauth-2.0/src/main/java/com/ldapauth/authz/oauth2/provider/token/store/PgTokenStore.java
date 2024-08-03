package com.ldapauth.authz.oauth2.provider.token.store;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import com.ldapauth.authz.oauth2.common.ExpiringOAuth2RefreshToken;
import com.ldapauth.authz.oauth2.common.OAuth2AccessToken;
import com.ldapauth.authz.oauth2.common.OAuth2RefreshToken;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;
import com.ldapauth.authz.oauth2.provider.token.AuthenticationKeyGenerator;
import com.ldapauth.authz.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import com.ldapauth.cache.CacheService;
import com.ldapauth.util.ObjectTransformer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Date;

/**
 * @author efenderbosch
 * @author crystal.sea  modify 2022/07/10
 */
public class PgTokenStore implements com.ldapauth.authz.oauth2.provider.token.TokenStore {
	static final Logger _logger = LoggerFactory.getLogger(PgTokenStore.class);

	private static final String PREFIX                 = "LDAPAUTH_OAUTH_V20_";

	private static final String ACCESS                 = PREFIX + "ACCESS_";
	private static final String AUTH_TO_ACCESS         = PREFIX + "AUTH_TO_ACCESS_";
	private static final String AUTH                   = PREFIX + "AUTH_";
	private static final String REFRESH_AUTH           = PREFIX + "REFRESH_AUTH_";
	private static final String ACCESS_TO_REFRESH      = PREFIX + "ACCESS_TO_REFRESH_";
	private static final String REFRESH                = PREFIX + "REFRESH_";
	private static final String REFRESH_TO_ACCESS      = PREFIX + "REFRESH_TO_ACCESS_";
	private static final String CLIENT_ID_TO_ACCESS    = PREFIX + "CLIENT_ID_TO_ACCESS_";
	private static final String UNAME_TO_ACCESS        = PREFIX + "UNAME_TO_ACCESS_";

	private CacheService cacheService;
	private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();



	public PgTokenStore(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		String key = authenticationKeyGenerator.extractKey(authentication);
		String serializedKey = (AUTH_TO_ACCESS + key);
		String value = cacheService.getCacheObject(serializedKey);
		if (StringUtils.isNotEmpty(value)) {
			OAuth2AccessToken accessToken = ObjectTransformer.deserialize(value);
			storeAccessToken(accessToken, authentication);
			return accessToken;
		}
		return null;
	}

	@Override
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		return readAuthentication(token.getValue());
	}

	@Override
	public OAuth2Authentication readAuthentication(String token) {
		String serializedKey = (AUTH + token);
		String value = cacheService.getCacheObject(serializedKey);
		if (StringUtils.isNotEmpty(value)) {
			OAuth2Authentication accessToken = ObjectTransformer.deserialize(value);
			return accessToken;
		}
		return null;
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		return readAuthenticationForRefreshToken(token.getValue());
	}

	public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
		String serializedKey = (REFRESH_AUTH + token);
		String value = cacheService.getCacheObject(serializedKey);
		if (StringUtils.isNotEmpty(value)) {
			OAuth2Authentication authentication = ObjectTransformer.deserialize(value);
			return authentication;
		}
		return null;
	}

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		String accessKey = (ACCESS + token.getValue());
		String authKey = (AUTH + token.getValue());
		String authToAccessKey = (AUTH_TO_ACCESS + authenticationKeyGenerator.extractKey(authentication));
		String approvalKey = (UNAME_TO_ACCESS + getApprovalKey(authentication));
		String clientId = (CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
		_logger.trace("accessKey " + accessKey);
		_logger.trace("authKey " + authKey);
		_logger.trace("authToAccessKey " + authToAccessKey);
		_logger.trace("approvalKey " + approvalKey);
		_logger.trace("clientId " + clientId);

		String tokenvalue = ObjectTransformer.serialize(token);
		String authenticationvalue = ObjectTransformer.serialize(authentication);

		cacheService.setCacheObject(accessKey, tokenvalue);
		cacheService.setCacheObject(authKey, authenticationvalue);
		cacheService.setCacheObject(authToAccessKey, tokenvalue);
		if (!authentication.isClientOnly()) {
			cacheService.setCacheObject(approvalKey, tokenvalue);
		}
		cacheService.setCacheObject(clientId, tokenvalue);
		if (token.getExpiration() != null) {
			int seconds = token.getExpiresIn();
			cacheService.expire(accessKey, seconds);
			cacheService.expire(authKey, seconds);
			cacheService.expire(authToAccessKey, seconds);
			cacheService.expire(clientId, seconds);
			cacheService.expire(approvalKey, seconds);
		}
		OAuth2RefreshToken refreshToken = token.getRefreshToken();
		if (refreshToken != null && refreshToken.getValue() != null) {
			String refresh = (token.getRefreshToken().getValue());
			String auth = (token.getValue());
			String refreshToAccessKey = (REFRESH_TO_ACCESS + token.getRefreshToken().getValue());
			_logger.trace("refreshToAccessKey " + refreshToAccessKey);
			cacheService.setCacheObject(refreshToAccessKey, auth);
			String accessToRefreshKey = (ACCESS_TO_REFRESH + token.getValue());
			_logger.trace("accessToRefreshKey " + accessToRefreshKey);
			cacheService.setCacheObject(accessToRefreshKey, refresh);
			if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
				ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
				Date expiration = expiringRefreshToken.getExpiration();
				if (expiration != null) {
					int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
							.intValue();
					cacheService.expire(refreshToAccessKey, seconds);
					cacheService.expire(accessToRefreshKey, seconds);
				}
			}
		}
	}

	private static String getApprovalKey(OAuth2Authentication authentication) {
		String userName = authentication.getUserAuthentication() == null ? ""
				: authentication.getUserAuthentication().getName();
		return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
	}

	private static String getApprovalKey(String clientId, String userName) {
		return clientId + (userName == null ? "" : "_" + userName);
	}

	@Override
	public void removeAccessToken(OAuth2AccessToken accessToken) {
		removeAccessToken(accessToken.getValue());
	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		String key = (ACCESS + tokenValue);
		String value = cacheService.getCacheObject(key);
		if (StringUtils.isNotEmpty(value)) {
			OAuth2AccessToken oAuth2AccessToken = ObjectTransformer.deserialize(value);
			return oAuth2AccessToken;
		}
		return null;
	}

	public void removeAccessToken(String tokenValue) {
		String accessKey = (ACCESS + tokenValue);
		String authKey = (AUTH + tokenValue);
		String accessToRefreshKey = (ACCESS_TO_REFRESH + tokenValue);

		String access = cacheService.getCacheObject(accessKey);
		String auth = cacheService.getCacheObject(authKey);
		cacheService.deleteObject(accessKey);
		cacheService.deleteObject(accessToRefreshKey);
		 //Don't remove the refresh token - it's up to the caller to do that
		cacheService.deleteObject(authKey);
		//List<Object> results = conn.closePipeline();
		//String access = (String) results.get(0);
		//String auth = (String) results.get(1);
		OAuth2Authentication authentication = ObjectTransformer.deserialize(auth);
		if (authentication != null) {
			String key = authenticationKeyGenerator.extractKey(authentication);
			String authToAccessKey = (AUTH_TO_ACCESS + key);
			String unameKey = (UNAME_TO_ACCESS + getApprovalKey(authentication));
			String clientId = (CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
			cacheService.deleteObject(authToAccessKey);
			cacheService.deleteObject(ACCESS + key);
		}

	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
		String refreshKey = (REFRESH + refreshToken.getValue());
		String refreshAuthKey = (REFRESH_AUTH + refreshToken.getValue());

		String refreshTokenvalue = ObjectTransformer.serialize(refreshToken);
		String authenticationvalue = ObjectTransformer.serialize(authentication);
		cacheService.setCacheObject(refreshKey, refreshTokenvalue);
		cacheService.setCacheObject(refreshAuthKey, authenticationvalue);

		if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
			ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
			Date expiration = expiringRefreshToken.getExpiration();
			if (expiration != null) {
				int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
						.intValue();
				cacheService.expire(refreshKey, seconds);
				cacheService.expire(refreshAuthKey, seconds);
			}
		}

	}

	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {
		String key = (REFRESH + tokenValue);
		String value = cacheService.getCacheObject(key);
		if (StringUtils.isNotEmpty(value)) {
			OAuth2RefreshToken refreshToken = ObjectTransformer.deserialize(value);
			return refreshToken;
		}
		return null;
	}

	@Override
	public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
		removeRefreshToken(refreshToken.getValue());
	}

	public void removeRefreshToken(String tokenValue) {
		String refreshKey = (REFRESH + tokenValue);
		String refreshAuthKey = (REFRESH_AUTH + tokenValue);
		String refresh2AccessKey = (REFRESH_TO_ACCESS + tokenValue);
		String access2RefreshKey = (ACCESS_TO_REFRESH + tokenValue);
		cacheService.deleteObject(refreshKey);
		cacheService.deleteObject(refreshAuthKey);
		cacheService.deleteObject(refresh2AccessKey);
		cacheService.deleteObject(access2RefreshKey);
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		removeAccessTokenUsingRefreshToken(refreshToken.getValue());
	}

	private void removeAccessTokenUsingRefreshToken(String refreshToken) {
		String key = (REFRESH_TO_ACCESS + refreshToken);
		String  accessToken = null;

		accessToken = cacheService.getCacheObject(key);
		cacheService.deleteObject(key);

		if (accessToken == null) {
			return;
		}
		//String  accessToken = (String) results.get(0);
		//String accessToken = ObjectTransformer.deserialize(bytes);
		if (accessToken != null) {
			removeAccessToken(accessToken);
		}

	}


	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
		String approvalKey = (UNAME_TO_ACCESS + getApprovalKey(clientId, userName));
		_logger.trace("approvalKey " + approvalKey);
		List<String> stringList = null;

		OAuth2AccessToken accessToken = null;

		stringList = cacheService.getCacheObject(approvalKey);
		if (stringList == null || stringList.size() == 0) {
			return Collections.<OAuth2AccessToken> emptySet();
		}
		List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>(stringList.size());
		for (String str : stringList) {
			//accessToken may expired
			String value = cacheService.getCacheObject(str);
			if(StringUtils.isNotEmpty(value)) {
				accessToken = ObjectTransformer.deserialize(value);
				accessTokens.add(accessToken);
			}
		}
		return Collections.<OAuth2AccessToken> unmodifiableCollection(accessTokens);

	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
		String key = (CLIENT_ID_TO_ACCESS + clientId);
		_logger.trace("TokensByClientId  " + key);
		List<String> stringList = cacheService.getCacheObject(key);

		if (stringList == null || stringList.size() == 0) {
			return Collections.<OAuth2AccessToken> emptySet();
		}
		List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>(stringList.size());
		for (String str : stringList) {
			String value = cacheService.getCacheObject(str);
			if(StringUtils.isNotEmpty(value)) {
				OAuth2AccessToken accessToken = ObjectTransformer.deserialize(value);
				accessTokens.add(accessToken);
			}
		}
		return Collections.<OAuth2AccessToken> unmodifiableCollection(accessTokens);
	}

}
