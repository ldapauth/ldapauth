package com.ldapauth.authz.oauth2.provider.token;

import java.util.Collections;
import java.util.List;

import com.ldapauth.authz.oauth2.common.OAuth2AccessToken;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;

/**
 * A composite token enhancer that loops over its delegate enhancers.
 *
 * @author Dave Syer
 *
 */
public class TokenEnhancerChain implements TokenEnhancer {

	private List<TokenEnhancer> delegates = Collections.emptyList();

	/**
	 * @param delegates the delegates to set
	 */
	public void setTokenEnhancers(List<TokenEnhancer> delegates) {
		this.delegates = delegates;
	}

	/**
	 * Loop over the {@link #setTokenEnhancers(List) delegates} passing the result into the next member of the chain.
	 *
	 * @see org.maxkey.authz.oauth2.provider.token.TokenEnhancer#enhance(org.maxkey.authz.oauth2.common.OAuth2AccessToken,
	 * org.maxkey.authz.oauth2.provider.OAuth2Authentication)
	 */
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		OAuth2AccessToken result = accessToken;
		for (TokenEnhancer enhancer : delegates) {
			result = enhancer.enhance(result, authentication);
		}
		return result;
	}

}
