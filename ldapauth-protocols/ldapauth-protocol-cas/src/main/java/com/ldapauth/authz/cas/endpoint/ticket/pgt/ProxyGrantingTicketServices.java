package com.ldapauth.authz.cas.endpoint.ticket.pgt;

import com.ldapauth.authz.cas.endpoint.ticket.RandomServiceTicketServices;
import com.ldapauth.authz.cas.endpoint.ticket.Ticket;
import com.ldapauth.cache.CacheService;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ProxyGrantingTicketServices extends RandomServiceTicketServices {


	protected int serviceTicketValiditySeconds = 60 * 60; //default 60 minutes.


	public static String PREFIX = "LDAPAUTH_CAS_TICKET_PGT_";

	CacheService cacheService;
	/**
	 * @param cacheService
	 */
	public ProxyGrantingTicketServices(CacheService cacheService) {
		super();
		this.cacheService = cacheService;
	}

	/**
	 *
	 */
	public ProxyGrantingTicketServices() {

	}

	@Override
	public void store(String ticketId, Ticket ticket) {
		store(ticketId,ticket,serviceTicketValiditySeconds);
	}

	@Override
	public void store(String ticketId, Ticket ticket, int validitySeconds) {
		cacheService.setCacheObject(prefixTicketId(ticketId), ticket,validitySeconds, TimeUnit.SECONDS);
	}

	@Override
	public Ticket remove(String ticketId) {
		Ticket ticket = cacheService.getCacheObject(prefixTicketId(ticketId));
		if(Objects.nonNull(ticket)) {
			cacheService.deleteObject(prefixTicketId(ticketId));
		}
		return ticket;
	}

	@Override
	public Ticket get(String ticketId) {
		return cacheService.getCacheObject(prefixTicketId(ticketId));
	}

	public String prefixTicketId(String ticketId) {
		return PREFIX + ticketId;
	}

}
