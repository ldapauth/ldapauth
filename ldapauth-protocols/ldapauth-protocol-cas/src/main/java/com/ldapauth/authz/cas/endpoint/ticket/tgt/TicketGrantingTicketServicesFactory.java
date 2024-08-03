package com.ldapauth.authz.cas.endpoint.ticket.tgt;

import com.ldapauth.authz.cas.endpoint.ticket.TicketServices;
import com.ldapauth.cache.CacheService;

public class TicketGrantingTicketServicesFactory {

    public TicketServices getService(CacheService cacheService) {
        return new TicketGrantingTicketServices(cacheService);
    }
}
