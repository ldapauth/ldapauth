package com.ldapauth.authz.oauth2.provider.authentication;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A holder of selected HTTP details related to an OAuth2 authentication request.
 *
 * @author Dave Syer
 *
 */
public class OAuth2AuthenticationDetails implements Serializable {

	private static final long serialVersionUID = -4809832298438307309L;

	public static final String ACCESS_TOKEN_VALUE = OAuth2AuthenticationDetails.class.getSimpleName() + ".ACCESS_TOKEN_VALUE";

	public static final String ACCESS_TOKEN_TYPE = OAuth2AuthenticationDetails.class.getSimpleName() + ".ACCESS_TOKEN_TYPE";

	private final String remoteAddress;

	private final String sessionId;

	private final String tokenValue;

	private final String tokenType;

	private final String display;

	private Object decodedDetails;

	/**
	 * Records the access token value and remote address and will also set the session Id if a session already exists
	 * (it won't create one).
	 *
	 * @param request that the authentication request was received from
	 */
	public OAuth2AuthenticationDetails(HttpServletRequest request) {
		this.tokenValue = (String) request.getAttribute(ACCESS_TOKEN_VALUE);
		this.tokenType = (String) request.getAttribute(ACCESS_TOKEN_TYPE);
		this.remoteAddress = request.getRemoteAddr();

		HttpSession session = request.getSession(false);
		this.sessionId = (session != null) ? session.getId() : null;
		StringBuilder builder = new StringBuilder();
		if (remoteAddress!=null) {
			builder.append("remoteAddress=").append(remoteAddress);
		}
		if (builder.length()>1) {
			builder.append(", ");
		}
		if (sessionId!=null) {
			builder.append("sessionId=<SESSION>");
			if (builder.length()>1) {
				builder.append(", ");
			}
		}

		if (tokenType!=null) {
			builder.append("tokenType=").append(this.tokenType);
		}
		if (tokenValue!=null) {
			builder.append("tokenValue=<TOKEN>");
		}
		this.display = builder.toString();
	}

	/**
	 * The access token value used to authenticate the request (normally in an authorization header).
	 *
	 * @return the tokenValue used to authenticate the request
	 */
	public String getTokenValue() {
		return tokenValue;
	}

	/**
	 * The access token type used to authenticate the request (normally in an authorization header).
	 *
	 * @return the tokenType used to authenticate the request if known
	 */
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * Indicates the TCP/IP address the authentication request was received from.
	 *
	 * @return the address
	 */
	public String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * Indicates the <code>HttpSession</code> id the authentication request was received from.
	 *
	 * @return the session ID
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * The authentication details obtained by decoding the access token
	 * if available.
	 *
	 * @return the decodedDetails if available (default null)
	 */
	public Object getDecodedDetails() {
		return decodedDetails;
	}

	/**
	 * The authentication details obtained by decoding the access token
	 * if available.
	 *
	 * @param decodedDetails the decodedDetails to set
	 */
	public void setDecodedDetails(Object decodedDetails) {
		this.decodedDetails = decodedDetails;
	}

	@Override
	public String toString() {
		return display;
	}

}
