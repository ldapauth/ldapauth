package com.ldapauth.authz.cas.endpoint.ticket;

/**
 * Marker interface for Services. Services are generally either remote
 * applications utilizing CAS or applications that principals wish to gain
 * access to. In most cases this will be some form of web application.
 *
 * @author William G. Thompson, Jr.
 * @author Scott Battaglia
 * @since 3.0
 * <p>
 * This is a published and supported CAS Server 3 API.
 * </p>
 */
public interface Service {
	 String getId();

    boolean matches(Service service);
}
