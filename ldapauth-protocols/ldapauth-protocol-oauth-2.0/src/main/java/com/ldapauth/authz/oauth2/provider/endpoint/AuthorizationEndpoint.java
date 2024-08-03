package com.ldapauth.authz.oauth2.provider.endpoint;

import java.net.URI;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ldapauth.authn.annotation.CurrentUser;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.authz.oauth2.common.OAuth2AccessToken;
import com.ldapauth.authz.oauth2.common.OAuth2Constants;
import com.ldapauth.authz.oauth2.common.exceptions.InvalidClientException;
import com.ldapauth.authz.oauth2.common.exceptions.InvalidRequestException;
import com.ldapauth.authz.oauth2.common.exceptions.OAuth2Exception;
import com.ldapauth.authz.oauth2.common.exceptions.RedirectMismatchException;
import com.ldapauth.authz.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import com.ldapauth.authz.oauth2.common.exceptions.UnsupportedResponseTypeException;
import com.ldapauth.authz.oauth2.common.util.OAuth2Utils;
import com.ldapauth.authz.oauth2.domain.ClientDetails;
import com.ldapauth.authz.oauth2.provider.AuthorizationRequest;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;
import com.ldapauth.authz.oauth2.provider.OAuth2Request;
import com.ldapauth.authz.oauth2.provider.OAuth2RequestValidator;
import com.ldapauth.authz.oauth2.provider.TokenRequest;
import com.ldapauth.authz.oauth2.provider.code.AuthorizationCodeServices;
import com.ldapauth.authz.oauth2.provider.implicit.ImplicitTokenRequest;
import com.ldapauth.authz.oauth2.provider.request.DefaultOAuth2RequestValidator;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.util.HttpEncoder;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * <p>
 * Implementation of the Authorization Endpoint from the OAuth2 specification. Accepts authorization requests, and
 * handles user approval if the grant type is authorization code. The tokens themselves are obtained from the
 * {@link TokenEndpoint Token Endpoint}, except in the implicit grant type (where they come from the Authorization
 * Endpoint via <code>response_type=token</code>.
 * </p>
 *
 * <p>
 * This endpoint should be secured so that it is only accessible to fully authenticated users (as a minimum requirement)
 * since it represents a request from a valid user to act on his or her behalf.
 * </p>
 *
 * @author Dave Syer
 * @author Vladimir Kryachko
 * @author Crystal.sea 2022-04-14
 *
 */
@Tag(name = "2-1-OAuth v2.0 API文档模块")
@Controller
public class AuthorizationEndpoint extends AbstractEndpoint {
	final static Logger _logger = LoggerFactory.getLogger(AuthorizationEndpoint.class);

	private static final String OAUTH_V20_AUTHORIZATION_URL = "" + OAuth2Constants.ENDPOINT.ENDPOINT_AUTHORIZE + "?client_id=%s&response_type=code&redirect_uri=%s&approval_prompt=auto";

	private RedirectResolver redirectResolver = new DefaultRedirectResolver();


	private OAuth2RequestValidator oauth2RequestValidator = new DefaultOAuth2RequestValidator();

	private String userApprovalPage = "forward:" + OAuth2Constants.ENDPOINT.ENDPOINT_APPROVAL_CONFIRM;

	private String errorPage = "forward:" + OAuth2Constants.ENDPOINT.ENDPOINT_ERROR;

	private Object implicitLock = new Object();


	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}

	@Operation(summary = "OAuth 2.0 认证接口", description = "传递参数应用ID，自动完成跳转认证拼接",method="GET")
    @RequestMapping(value = {OAuth2Constants.ENDPOINT.ENDPOINT_BASE + "/{id}"},method = RequestMethod.GET)
    public ModelAndView authorize(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("id") String id){
        ClientDetails clientDetails =getClientDetailsService().loadClientByClientId(id,true);
        _logger.debug(""+clientDetails);
        String authorizationUrl = "";
        try {
            authorizationUrl = String.format(OAUTH_V20_AUTHORIZATION_URL,
                            clientDetails.getClientId(),
                            HttpEncoder.encode(clientDetails.getRegisteredRedirectUri().toArray()[0].toString())
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }

        _logger.debug("authorizationUrl {}" , authorizationUrl);

        return WebContext.redirect(authorizationUrl);
    }

	@Operation(summary = "OAuth 2.0 认证接口", description = "传递参数client_id,response_type,redirect_uri等",method="GET")
	@RequestMapping(value = {
								OAuth2Constants.ENDPOINT.ENDPOINT_AUTHORIZE,
								OAuth2Constants.ENDPOINT.ENDPOINT_TENCENT_IOA_AUTHORIZE
							},
					method = RequestMethod.GET)
	public ModelAndView authorize(
	            Map<String, Object> model,
	            @RequestParam Map<String, String> parameters,
	            @CurrentUser UserInfo currentUser,
	            SessionStatus sessionStatus) {

		 Principal principal=(Principal) AuthorizationUtils.getAuthentication();
		// Pull out the authorization request first, using the OAuth2RequestFactory. All further logic should
		// query off of the authorization request instead of referring back to the parameters map. The contents of the
		// parameters map will be stored without change in the AuthorizationRequest object once it is created.
		AuthorizationRequest authorizationRequest = getOAuth2RequestFactory().createAuthorizationRequest(parameters);

		Set<String> responseTypes = authorizationRequest.getResponseTypes();

		if (!responseTypes.contains(OAuth2Constants.PARAMETER.TOKEN) && !responseTypes.contains(OAuth2Constants.PARAMETER.CODE)) {
			throw new UnsupportedResponseTypeException("Unsupported response types: " + responseTypes);
		}

		if (authorizationRequest.getClientId() == null) {
			throw new InvalidClientException("A client id must be provided");
		}

		try {

			if (!(principal instanceof Authentication) || !((Authentication) principal).isAuthenticated()) {
				throw new InsufficientAuthenticationException(
						"User must be authenticated with Spring Security before authorization can be completed.");
			}

			ClientDetails client = getClientDetailsService().loadClientByClientId(authorizationRequest.getClientId(),true);

			// The resolved redirect URI is either the redirect_uri from the parameters or the one from
			// clientDetails. Either way we need to store it on the AuthorizationRequest.
			String redirectUriParameter = authorizationRequest.getRequestParameters().get(OAuth2Constants.PARAMETER.REDIRECT_URI);
			String resolvedRedirect = redirectResolver.resolveRedirect(redirectUriParameter, client);
			if (!StringUtils.hasText(resolvedRedirect)) {
				logger.info("Client redirectUri "+resolvedRedirect);
				logger.info("Parameter redirectUri "+redirectUriParameter);

				throw new RedirectMismatchException(
						"A redirectUri must be either supplied or preconfigured in the ClientDetails");
			}
			authorizationRequest.setRedirectUri(resolvedRedirect);

			// We intentionally only validate the parameters requested by the client (ignoring any data that may have
			// been added to the request by the manager).
			oauth2RequestValidator.validateScope(authorizationRequest, client);
			// is this call necessary?
			authorizationRequest.setApproved(true);
			// Validation is all done, so we can check for auto approval...
			if (responseTypes.contains(OAuth2Constants.PARAMETER.TOKEN)) {
				return new ModelAndView(getImplicitGrantResponse(authorizationRequest));
			}
			if (responseTypes.contains(OAuth2Constants.PARAMETER.CODE)) {
				return WebContext.redirect(getAuthorizationCodeResponse(authorizationRequest,
						(Authentication) principal));
			}
		} catch (RuntimeException e) {
			sessionStatus.setComplete();
			throw e;
		}
		throw new RedirectMismatchException(
				"A redirectUri must be either supplied or preconfigured in the ClientDetails");
	}


	// We can grant a token and return it with implicit approval.
	private String  getImplicitGrantResponse(AuthorizationRequest authorizationRequest) {
		try {
			TokenRequest tokenRequest = getOAuth2RequestFactory().createTokenRequest(authorizationRequest, "implicit");
			OAuth2Request storedOAuth2Request = getOAuth2RequestFactory().createOAuth2Request(authorizationRequest);

			OAuth2AccessToken accessToken = getAccessTokenForImplicitGrant(tokenRequest, storedOAuth2Request);
			if (accessToken == null) {
				throw new UnsupportedResponseTypeException("Unsupported response type: token");
			}
			return appendAccessToken(authorizationRequest, accessToken);
		}
		catch (OAuth2Exception e) {
			return getUnsuccessfulRedirect(authorizationRequest, e, true);
		}
	}

	private OAuth2AccessToken getAccessTokenForImplicitGrant(TokenRequest tokenRequest,
			OAuth2Request storedOAuth2Request) {
		OAuth2AccessToken accessToken = null;
		// These 1 method calls have to be atomic, otherwise the ImplicitGrantService can have a race condition where
		// one thread removes the token request before another has a chance to redeem it.
		synchronized (this.implicitLock) {
			accessToken = getTokenGranter().grant("implicit", new ImplicitTokenRequest(tokenRequest, storedOAuth2Request));
		}
		return accessToken;
	}

	// Authorization Code Response
	private String getAuthorizationCodeResponse(AuthorizationRequest authorizationRequest, Authentication authUser) {
		try {
			String  successfulRedirect = getSuccessfulRedirect(
					authorizationRequest,
					generateCode(authorizationRequest, authUser)
			);
			_logger.debug("successfulRedirect " + successfulRedirect);
			return successfulRedirect;
		}
		catch (OAuth2Exception e) {
			return getUnsuccessfulRedirect(authorizationRequest, e, false);
		}
	}

	private String appendAccessToken(AuthorizationRequest authorizationRequest, OAuth2AccessToken accessToken) {

		Map<String, Object> vars = new HashMap<String, Object>();

		String requestedRedirect = authorizationRequest.getRedirectUri();
		if (accessToken == null) {
			throw new InvalidRequestException("An implicit grant could not be made");
		}
		StringBuilder url = new StringBuilder(requestedRedirect);
		if (requestedRedirect.contains("#")) {
			url.append("&");
		}
		else {
			url.append("#");
		}

		url.append(templateUrlVar(OAuth2Constants.PARAMETER.ACCESS_TOKEN));
		url.append("&").append(templateUrlVar(OAuth2Constants.PARAMETER.TOKEN_TYPE));
		vars.put(OAuth2Constants.PARAMETER.ACCESS_TOKEN, accessToken.getValue());
		vars.put(OAuth2Constants.PARAMETER.TOKEN_TYPE, accessToken.getTokenType());
		String state = authorizationRequest.getState();

		if (state != null) {
			url.append("&").append(templateUrlVar(OAuth2Constants.PARAMETER.STATE));
			vars.put(OAuth2Constants.PARAMETER.STATE, state);
		}
		Date expiration = accessToken.getExpiration();
		if (expiration != null) {
			long expires_in = (expiration.getTime() - System.currentTimeMillis()) / 1000;
			url.append("&").append(templateUrlVar(OAuth2Constants.PARAMETER.EXPIRES_IN));
			vars.put(OAuth2Constants.PARAMETER.EXPIRES_IN, expires_in);
		}
		String originalScope = authorizationRequest.getRequestParameters().get(OAuth2Constants.PARAMETER.SCOPE);
		if (originalScope == null || !OAuth2Utils.parseParameterList(originalScope).equals(accessToken.getScope())) {
			url.append("&").append(templateUrlVar(OAuth2Constants.PARAMETER.SCOPE));
			vars.put(OAuth2Constants.PARAMETER.SCOPE, OAuth2Utils.formatParameterList(accessToken.getScope()));
		}
		Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
		for (String key : additionalInformation.keySet()) {
			Object value = additionalInformation.get(key);
			if (value != null) {
				url.append("&" + key + "={extra_" + key + "}");
				vars.put("extra_" + key, value);
			}
		}
		UriTemplate template = new UriTemplate(url.toString());
		// Do not include the refresh token (even if there is one)
		return template.expand(vars).toString();
	}

	public String templateUrlVar(String parameterName) {
		return parameterName + "={" + parameterName + "}";
	}

	private String generateCode(AuthorizationRequest authorizationRequest, Authentication authentication)
			throws AuthenticationException {

		try {

			OAuth2Request storedOAuth2Request = getOAuth2RequestFactory().createOAuth2Request(authorizationRequest);

			OAuth2Authentication combinedAuth = new OAuth2Authentication(storedOAuth2Request, authentication);
			String code = authorizationCodeServices.createAuthorizationCode(combinedAuth);

			return code;

		}
		catch (OAuth2Exception e) {

			if (authorizationRequest.getState() != null) {
				e.addAdditionalInformation(OAuth2Constants.PARAMETER.STATE, authorizationRequest.getState());
			}

			throw e;

		}
	}
	// Successful Redirect
	private String getSuccessfulRedirect(AuthorizationRequest authorizationRequest, String authorizationCode) {

		if (authorizationCode == null) {
			throw new IllegalStateException("No authorization code found in the current request scope.");
		}

		Map<String, String> query = new LinkedHashMap<String, String>();
		query.put(OAuth2Constants.PARAMETER.CODE, authorizationCode);

		String state = authorizationRequest.getState();
		if (state != null) {
			query.put(OAuth2Constants.PARAMETER.STATE, state);
		}

		//this is for cas
		String service = authorizationRequest.getRequestParameters().get("service");
		if (service != null) {
			query.put("service", service);
		}

		return append(authorizationRequest.getRedirectUri(), query, false);
	}

	private String getUnsuccessfulRedirect(AuthorizationRequest authorizationRequest, OAuth2Exception failure,
			boolean fragment) {

		if (authorizationRequest == null || authorizationRequest.getRedirectUri() == null) {
			// we have no redirect for the user. very sad.
			throw new UnapprovedClientAuthenticationException("Authorization failure, and no redirect URI.", failure);
		}

		Map<String, String> query = new LinkedHashMap<String, String>();

		query.put("error", failure.getOAuth2ErrorCode());
		query.put("error_description", failure.getMessage());

		if (authorizationRequest.getState() != null) {
			query.put(OAuth2Constants.PARAMETER.STATE, authorizationRequest.getState());
		}

		if (failure.getAdditionalInformation() != null) {
			for (Map.Entry<String, String> additionalInfo : failure.getAdditionalInformation().entrySet()) {
				query.put(additionalInfo.getKey(), additionalInfo.getValue());
			}
		}

		return append(authorizationRequest.getRedirectUri(), query, fragment);

	}

	private String append(String base, Map<String, ?> query, boolean fragment) {
		return append(base, query, null, fragment);
	}

	private String append(String base, Map<String, ?> query, Map<String, String> keys, boolean fragment) {

		UriComponentsBuilder template = UriComponentsBuilder.newInstance();
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base);
		URI redirectUri;
		try {
			// assume it's encoded to start with (if it came in over the wire)
			redirectUri = builder.build(true).toUri();
		}
		catch (Exception e) {
			// ... but allow client registrations to contain hard-coded non-encoded values
			redirectUri = builder.build().toUri();
			builder = UriComponentsBuilder.fromUri(redirectUri);
		}
		template.scheme(redirectUri.getScheme()).port(redirectUri.getPort()).host(redirectUri.getHost())
				.userInfo(redirectUri.getUserInfo()).path(redirectUri.getPath());

		if (fragment) {
			StringBuilder values = new StringBuilder();
			if (redirectUri.getFragment() != null) {
				String append = redirectUri.getFragment();
				values.append(append);
			}
			for (String key : query.keySet()) {
				if (values.length() > 0) {
					values.append("&");
				}
				String name = key;
				if (keys != null && keys.containsKey(key)) {
					name = keys.get(key);
				}
				values.append(name + "={" + key + "}");
			}
			if (values.length() > 0) {
				template.fragment(values.toString());
			}
			UriComponents encoded = template.build().expand(query).encode();
			builder.fragment(encoded.getFragment());
		}
		else {
			for (String key : query.keySet()) {
				String name = key;
				if (keys != null && keys.containsKey(key)) {
					name = keys.get(key);
				}
				template.queryParam(name, "{" + key + "}");
			}
			template.fragment(redirectUri.getFragment());
			UriComponents encoded = template.build().expand(query).encode();
			builder.query(encoded.getQuery());
		}

		return builder.build().toUriString();

	}

	public void setUserApprovalPage(String userApprovalPage) {
		this.userApprovalPage = userApprovalPage;
	}

	public void setAuthorizationCodeServices(AuthorizationCodeServices authorizationCodeServices) {
		this.authorizationCodeServices = authorizationCodeServices;
	}

	public void setRedirectResolver(RedirectResolver redirectResolver) {
		this.redirectResolver = redirectResolver;
	}

	public void setOAuth2RequestValidator(OAuth2RequestValidator oauth2RequestValidator) {
		this.oauth2RequestValidator = oauth2RequestValidator;
	}

}
