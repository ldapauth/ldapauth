package com.ldapauth.authz.oauth2.provider;

import java.util.ArrayList;
import java.util.List;

import com.ldapauth.authz.oauth2.common.OAuth2AccessToken;

/**
 * @author Dave Syer
 *
 */
public class CompositeTokenGranter implements TokenGranter {

	private final List<TokenGranter> tokenGranters;

	public CompositeTokenGranter(List<TokenGranter> tokenGranters) {
		this.tokenGranters = new ArrayList<TokenGranter>(tokenGranters);
	}

	public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
		for (TokenGranter granter : tokenGranters) {
			OAuth2AccessToken grant = granter.grant(grantType, tokenRequest);
			if (grant!=null) {
				return grant;
			}
		}
		return null;
	}

}
