package com.ldapauth.authz.oauth2.provider.refresh;

import com.ldapauth.authz.oauth2.common.OAuth2AccessToken;
import com.ldapauth.authz.oauth2.domain.ClientDetails;
import com.ldapauth.authz.oauth2.provider.ClientDetailsService;
import com.ldapauth.authz.oauth2.provider.OAuth2RequestFactory;
import com.ldapauth.authz.oauth2.provider.TokenRequest;
import com.ldapauth.authz.oauth2.provider.token.AbstractTokenGranter;
import com.ldapauth.authz.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * @author Dave Syer
 *
 */
public class RefreshTokenGranter extends AbstractTokenGranter {

	private static final String GRANT_TYPE = "refresh_token";

	public RefreshTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
	}

	@Override
	protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
		String refreshToken = tokenRequest.getRequestParameters().get("refresh_token");
		return getTokenServices().refreshAccessToken(refreshToken, tokenRequest);
	}

}
