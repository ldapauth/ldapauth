package com.ldapauth.authz.oauth2.provider.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

/**
 * A source for authentication details in an OAuth2 protected Resource.
 *
 * @author Dave Syer
 *
 */
public class OAuth2AuthenticationDetailsSource implements
		AuthenticationDetailsSource<HttpServletRequest, OAuth2AuthenticationDetails> {

	public OAuth2AuthenticationDetails buildDetails(HttpServletRequest context) {
		return new OAuth2AuthenticationDetails(context);
	}

}
