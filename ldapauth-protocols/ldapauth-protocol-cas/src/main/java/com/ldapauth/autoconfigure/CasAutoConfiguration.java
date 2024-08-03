package com.ldapauth.autoconfigure;

import com.ldapauth.authz.cas.endpoint.ticket.TicketServices;
import com.ldapauth.authz.cas.endpoint.ticket.pgt.ProxyGrantingTicketServicesFactory;
import com.ldapauth.authz.cas.endpoint.ticket.st.TicketServicesFactory;
import com.ldapauth.authz.cas.endpoint.ticket.tgt.TicketGrantingTicketServicesFactory;
import com.ldapauth.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = {
        "com.ldapauth.authz.cas.endpoint"
})
public class CasAutoConfiguration implements InitializingBean {
    private static final  Logger _logger = LoggerFactory.getLogger(CasAutoConfiguration.class);

    /**
     * casTicketServices.
     */
    @Bean(name = "casTicketServices")
    public TicketServices casTicketServices(CacheService cacheService) {
    	_logger.debug("init casTicketServices.");
        return new TicketServicesFactory().getService(cacheService);
    }

    /**
     * casTicketGrantingTicketServices.
     */
    @Bean(name = "casTicketGrantingTicketServices")
    public TicketServices casTicketGrantingTicketServices(CacheService cacheService) {
    	_logger.debug("init casTicketGrantingTicketServices.");
        return new TicketGrantingTicketServicesFactory().getService(cacheService);
    }

    /**
     * casProxyGrantingTicketServices.
     */
    @Bean(name = "casProxyGrantingTicketServices")
    public TicketServices casProxyGrantingTicketServices(CacheService cacheService) {
    	_logger.debug("init casTicketGrantingTicketServices.");
        return new ProxyGrantingTicketServicesFactory().getService(cacheService);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
