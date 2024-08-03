package com.ldapauth.authz.oauth2.provider.endpoint;

import com.ldapauth.authz.oauth2.common.exceptions.OAuth2Exception;
import com.ldapauth.authz.oauth2.domain.ClientDetails;

/**
 * Basic interface for determining the redirect URI for a user agent.
 *
 * @author Ryan Heaton
 */
public interface RedirectResolver {

  /**
   * Resolve the redirect for the specified client.
   *
   * @param requestedRedirect The redirect that was requested (may not be null).
   * @param client The client for which we're resolving the redirect.
   * @return The resolved redirect URI.
   * @throws OAuth2Exception If the requested redirect is invalid for the specified client.
   */
  String resolveRedirect(String requestedRedirect, ClientDetails client) throws OAuth2Exception;

}
