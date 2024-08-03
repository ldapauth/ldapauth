package com.ldapauth.authz.oauth2.provider.token;


/**
 * @author Dave Syer
 *
 */
public interface ConsumerTokenServices {

	boolean revokeToken(String tokenValue);

}
