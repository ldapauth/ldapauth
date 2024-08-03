package com.ldapauth.authz.cas.endpoint.ticket.st;

import com.ldapauth.authz.cas.endpoint.ticket.RandomServiceTicketServices;
import com.ldapauth.authz.cas.endpoint.ticket.Ticket;
import com.ldapauth.cache.CacheService;
import com.ldapauth.util.ObjectTransformer;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class TicketServices extends RandomServiceTicketServices {


	protected int serviceTicketValiditySeconds = 60 * 10; //default 10 minutes.

	CacheService cacheService;

	public static String PREFIX = "LDAPAUTH_CAS_TICKET_ST_";

	/**
	 * @param cacheService
	 */
	public TicketServices(CacheService cacheService) {
		super();
		this.cacheService = cacheService;
	}

	/**
	 *
	 */
	public TicketServices() {

	}

	@Override
	public void store(String ticketId, Ticket ticket) {
		store(ticketId,ticket,serviceTicketValiditySeconds);
	}

	@Override
	public void store(String ticketId, Ticket ticket, int validitySeconds) {
		String ticietValue = ObjectTransformer.serialize(ticket);
		cacheService.setCacheObject(prefixTicketId(ticketId), ticietValue,validitySeconds, TimeUnit.SECONDS);
	}

	@Override
	public Ticket remove(String ticketId) {
		String value = cacheService.getCacheObject(prefixTicketId(ticketId));
		if (StringUtils.isNotEmpty(value)) {
			Ticket ticket = ObjectTransformer.deserialize(value);
			cacheService.deleteObject(prefixTicketId(ticketId));
			return ticket;
		}
		return null;
	}

	@Override
	public Ticket get(String ticketId) {
		String value = cacheService.getCacheObject(prefixTicketId(ticketId));
		if (StringUtils.isNotEmpty(value)) {
			Ticket ticket = ObjectTransformer.deserialize(value);
			return ticket;
		}
		return null;
	}

    public String prefixTicketId(String ticketId) {
    	return PREFIX + ticketId;
    }


}
