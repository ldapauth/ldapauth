package com.ldapauth.authz.oauth2.provider.token;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ldapauth.authz.oauth2.common.util.OAuth2Utils;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;
import com.ldapauth.authz.oauth2.provider.OAuth2Request;

/**
 * Basic key generator taking into account the client id, scope, reource ids and username (principal name) if they
 * exist.
 *
 * @author Dave Syer
 *
 */
public class DefaultAuthenticationKeyGenerator implements AuthenticationKeyGenerator {

	private static final String CLIENT_ID = "client_id";

	private static final String SCOPE = "scope";

	private static final String USERNAME = "username";

	public String extractKey(OAuth2Authentication authentication) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		OAuth2Request authorizationRequest = authentication.getOAuth2Request();
		if (!authentication.isClientOnly()) {
			values.put(USERNAME, authentication.getName());
		}
		values.put(CLIENT_ID, authorizationRequest.getClientId());
		if (authorizationRequest.getScope() != null) {
			values.put(SCOPE, OAuth2Utils.formatParameterList(authorizationRequest.getScope()));
		}
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}

		try {
			byte[] bytes = digest.digest(values.toString().getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}

}
