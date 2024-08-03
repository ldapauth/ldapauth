package com.ldapauth.authz.cas.endpoint.ticket.st;

import com.ldapauth.cache.CacheService;

public class TicketServicesFactory {

    public TicketServices getService(
            CacheService cacheService) {
        return new TicketServices(cacheService);
    }
}
