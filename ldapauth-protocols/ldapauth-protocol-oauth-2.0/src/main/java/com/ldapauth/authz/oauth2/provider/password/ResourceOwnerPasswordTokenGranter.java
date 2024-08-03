package com.ldapauth.authz.oauth2.provider.password;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ldapauth.authz.oauth2.common.exceptions.InvalidGrantException;
import com.ldapauth.authz.oauth2.domain.ClientDetails;
import com.ldapauth.authz.oauth2.provider.ClientDetailsService;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;
import com.ldapauth.authz.oauth2.provider.OAuth2Request;
import com.ldapauth.authz.oauth2.provider.OAuth2RequestFactory;
import com.ldapauth.authz.oauth2.provider.TokenRequest;
import com.ldapauth.authz.oauth2.provider.token.AbstractTokenGranter;
import com.ldapauth.authz.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * @author Dave Syer
 *
 */
public class ResourceOwnerPasswordTokenGranter extends AbstractTokenGranter {

	private static final String GRANT_TYPE = "password";

	private final AuthenticationManager authenticationManager;

	public ResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager,
			AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

		Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
		String username = parameters.get("username");
		String password = parameters.get("password");
		// Protect from downstream leaks of password
		parameters.remove("password");

		Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
		try {
			userAuth = authenticationManager.authenticate(userAuth);
		}
		catch (AccountStatusException ase) {
			//covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
			throw new InvalidGrantException(ase.getMessage());
		}
		catch (BadCredentialsException e) {
			// If the username/password are wrong the spec says we should send 400/invlid grant
			throw new InvalidGrantException(e.getMessage());
		}
		if (userAuth == null || !userAuth.isAuthenticated()) {
			throw new InvalidGrantException("Could not authenticate user: " + username);
		}

		OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
		return new OAuth2Authentication(storedOAuth2Request, userAuth);
	}
}
