package com.ldapauth.authz.oauth2.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * @author Dave Syer
 *
 */
public interface OAuth2RefreshToken  extends Serializable {

	/**
	 * The value of the token.
	 *
	 * @return The value of the token.
	 */
	@JsonValue
	String getValue();

}
