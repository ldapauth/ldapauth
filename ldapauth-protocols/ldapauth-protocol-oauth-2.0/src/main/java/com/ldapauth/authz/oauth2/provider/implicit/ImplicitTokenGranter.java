package com.ldapauth.authz.oauth2.provider.implicit;

import com.ldapauth.authz.oauth2.domain.ClientDetails;
import com.ldapauth.authz.oauth2.provider.ClientDetailsService;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;
import com.ldapauth.authz.oauth2.provider.OAuth2Request;
import com.ldapauth.authz.oauth2.provider.OAuth2RequestFactory;
import com.ldapauth.authz.oauth2.provider.TokenRequest;
import com.ldapauth.authz.oauth2.provider.token.AbstractTokenGranter;
import com.ldapauth.authz.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

/**
 * @author Dave Syer
 *
 */
public class ImplicitTokenGranter extends AbstractTokenGranter {

	private static final String GRANT_TYPE = "implicit";

	public ImplicitTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest clientToken) {

		Authentication userAuth = SecurityContextHolder.getContext().getAuthentication();
		if (userAuth==null || !userAuth.isAuthenticated()) {
			throw new InsufficientAuthenticationException("There is no currently logged in user");
		}
		Assert.state(clientToken instanceof ImplicitTokenRequest, "An ImplicitTokenRequest is required here. Caller needs to wrap the TokenRequest.");

		OAuth2Request requestForStorage = ((ImplicitTokenRequest)clientToken).getOAuth2Request();

		return new OAuth2Authentication(requestForStorage, userAuth);

	}

	@SuppressWarnings("deprecation")
	public void setImplicitGrantService(ImplicitGrantService service) {
	}

}
