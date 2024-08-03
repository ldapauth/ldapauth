package com.ldapauth.authz.oauth2.provider.implicit;

import com.ldapauth.authz.oauth2.provider.OAuth2Request;
import com.ldapauth.authz.oauth2.provider.TokenRequest;

/**
 * @author Dave Syer
 *
 * @since 2.0.2
 *
 */
@SuppressWarnings("serial")
public class ImplicitTokenRequest extends TokenRequest {

	private OAuth2Request oauth2Request;

	public ImplicitTokenRequest(TokenRequest tokenRequest, OAuth2Request oauth2Request) {
		super(tokenRequest.getRequestParameters(), tokenRequest.getClientId(), tokenRequest.getScope(), tokenRequest.getGrantType());
		this.oauth2Request = oauth2Request;
	}

	public OAuth2Request getOAuth2Request() {
		return oauth2Request;
	}

}
