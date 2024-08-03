package com.ldapauth.authz.oauth2.common;

import java.util.Date;

/**
 * @author Dave Syer
 *
 */
public interface ExpiringOAuth2RefreshToken extends OAuth2RefreshToken {

	Date getExpiration();

}
