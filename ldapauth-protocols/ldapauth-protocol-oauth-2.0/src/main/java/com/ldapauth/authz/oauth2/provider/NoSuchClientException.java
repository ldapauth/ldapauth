package com.ldapauth.authz.oauth2.provider;

/**
 * @author Dave Syer
 *
 */
@SuppressWarnings("serial")
public class NoSuchClientException extends ClientRegistrationException {

	public NoSuchClientException(String msg) {
		super(msg);
	}

	public NoSuchClientException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
