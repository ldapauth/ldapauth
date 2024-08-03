package com.ldapauth.authz.oauth2.provider;

import com.ldapauth.authz.oauth2.common.OAuth2AccessToken;

/**
 * Interface for granters of access tokens. Various grant types are defined in the specification, and each of those has
 * an implementation, leaving room for extensions to the specification as needed.
 *
 * @author Dave Syer
 *
 */
public interface TokenGranter {

	OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest);

}
