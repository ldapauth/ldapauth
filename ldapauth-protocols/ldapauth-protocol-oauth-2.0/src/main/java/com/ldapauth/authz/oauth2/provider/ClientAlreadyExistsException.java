package com.ldapauth.authz.oauth2.provider;

/**
 * Exception indicating that a client registration already exists (e.g. if someone tries to create a duplicate).
 *
 * @author Dave Syer
 *
 */
@SuppressWarnings("serial")
public class ClientAlreadyExistsException extends ClientRegistrationException {

	public ClientAlreadyExistsException(String msg) {
		super(msg);
	}

	public ClientAlreadyExistsException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
