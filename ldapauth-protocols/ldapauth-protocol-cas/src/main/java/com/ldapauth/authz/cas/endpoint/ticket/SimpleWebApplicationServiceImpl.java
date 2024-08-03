package com.ldapauth.authz.cas.endpoint.ticket;

/**
 * Represents a service which wishes to use the CAS protocol.
 *
 * @author Scott Battaglia
 * @since 3.1
 */
public final class SimpleWebApplicationServiceImpl extends AbstractWebApplicationService {

    public SimpleWebApplicationServiceImpl(final String id) {
        this(id, id, null);
    }

    private SimpleWebApplicationServiceImpl(final String id,
        final String originalUrl, final String artifactId) {
        super(id, originalUrl, artifactId);
    }



}
