package com.ldapauth.authz.oauth2.provider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Strategy for accessing useful information about the current security context.
 *
 * @author Dave Syer
 *
 */
public class DefaultSecurityContextAccessor implements SecurityContextAccessor {

	@Override
	public boolean isUser() {
		Authentication authentication = getUserAuthentication();
		return authentication != null;
	}

	@Override
	public Set<GrantedAuthority> getAuthorities() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(new HashSet<GrantedAuthority>(authentication.getAuthorities()));
	}

	private Authentication getUserAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		if (authentication instanceof OAuth2Authentication) {
			OAuth2Authentication oauth = (OAuth2Authentication) authentication;
			return oauth.getUserAuthentication();
		}
		return authentication;
	}

}
