package com.ldapauth.authz.oauth2.provider.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

/**
 * @author Dave Syer
 *
 */
public interface TokenExtractor {

	/**
	 * Extract a token value from an incoming request without authentication.
	 *
	 * @param request the current ServletRequest
	 * @return an authentication token whose principal is an access token (or null if there is none)
	 */
	Authentication extract(HttpServletRequest request);

}
