package com.ldapauth.authz.oauth2.provider.client;

import com.ldapauth.authz.oauth2.common.DefaultOAuth2AccessToken;
import com.ldapauth.authz.oauth2.common.OAuth2AccessToken;
import com.ldapauth.authz.oauth2.provider.ClientDetailsService;
import com.ldapauth.authz.oauth2.provider.OAuth2RequestFactory;
import com.ldapauth.authz.oauth2.provider.TokenRequest;
import com.ldapauth.authz.oauth2.provider.token.AbstractTokenGranter;
import com.ldapauth.authz.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * @author Dave Syer
 *
 */
public class ClientCredentialsTokenGranter extends AbstractTokenGranter {

	private static final String GRANT_TYPE = "client_credentials";
	private boolean allowRefresh = false;

	public ClientCredentialsTokenGranter(AuthorizationServerTokenServices tokenServices,
			ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
	}

	public void setAllowRefresh(boolean allowRefresh) {
		this.allowRefresh = allowRefresh;
	}

	@Override
	public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
		OAuth2AccessToken token = super.grant(grantType, tokenRequest);
		if (token != null) {
			DefaultOAuth2AccessToken norefresh = new DefaultOAuth2AccessToken(token);
			// The spec says that client credentials should not be allowed to get a refresh token
			if (!allowRefresh) {
				norefresh.setRefreshToken(null);
			}
			token = norefresh;
		}
		return token;
	}

}
