package com.ldapauth.authz.oauth2.provider.token;

import com.ldapauth.authz.oauth2.common.OAuth2AccessToken;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;

/**
 * Strategy for enhancing an access token before it is stored by an {@link AuthorizationServerTokenServices}
 * implementation.
 *
 * @author Dave Syer
 *
 */
public interface TokenEnhancer {

	/**
	 * Provides an opportunity for customization of an access token (e.g. through its additional information map) during
	 * the process of creating a new token for use by a client.
	 *
	 * @param accessToken the current access token with its expiration and refresh token
	 * @param authentication the current authentication including client and user details
	 * @return a new token enhanced with additional information
	 */
	OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication);

}
