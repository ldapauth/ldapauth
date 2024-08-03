package com.ldapauth.authz.oauth2.provider;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

/**
 * Strategy for accessing useful information about the current security context.
 *
 * @author Dave Syer
 *
 */
public interface SecurityContextAccessor {

	/**
	 * @return true if the current context represents a user
	 */
	boolean isUser();

	/**
	 * Get the current granted authorities (never null)
	 */
	Set<GrantedAuthority> getAuthorities();

}
