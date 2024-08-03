package com.ldapauth.authz.cas.endpoint.ticket.pgt;

import com.ldapauth.authz.cas.endpoint.ticket.TicketServices;
import com.ldapauth.cache.CacheService;

public class ProxyGrantingTicketServicesFactory {

    public TicketServices getService(CacheService cacheService) {
    	TicketServices  casTicketServices = new ProxyGrantingTicketServices(cacheService);
        return casTicketServices;
    }
}
