package com.ldapauth.authz.oauth2.provider.token;

import java.util.Map;

import org.springframework.security.core.Authentication;

/**
 * Utility interface for converting a user authentication to and from a Map.
 *
 * @author Dave Syer
 *
 */
public interface UserAuthenticationConverter {

	final String AUTHORITIES = "authorities";

	final String USERNAME = "user_name";

	/**
	 * Extract information about the user to be used in an access token (i.e. for resource servers).
	 *
	 * @param userAuthentication an authentication representing a user
	 * @return a map of key values representing the unique information about the user
	 */
	Map<String, ?> convertUserAuthentication(Authentication userAuthentication);

	/**
	 * Inverse of {@link #convertUserAuthentication(Authentication)}. Extracts an Authentication from a map.
	 *
	 * @param map a map of user information
	 * @return an Authentication representing the user or null if there is none
	 */
	Authentication extractAuthentication(Map<String, ?> map);

}
