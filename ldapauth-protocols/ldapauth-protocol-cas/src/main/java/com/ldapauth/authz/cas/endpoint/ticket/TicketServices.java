package com.ldapauth.authz.cas.endpoint.ticket;

public interface TicketServices {

	/**
	 * Create a authorization code for the specified authentications.
	 *
	 * @param authentication The authentications to store.
	 * @return The generated code.
	 */
	String createTicket(Ticket ticket);

	String createTicket(Ticket ticket , int validitySeconds);

	/**
	 * Consume a authorization code.
	 *
	 * @param code The authorization code to consume.
	 * @return The authentications associated with the code.
	 * @throws InvalidGrantException If the authorization code is invalid or expired.
	 */
	Ticket consumeTicket(String ticketId)
			throws Exception;

	public  void store(String ticketId, Ticket ticket);

	public  void store(String ticketId, Ticket ticket, int validitySeconds);

    public  Ticket remove(String ticket);

    public  Ticket get(String ticketId);

}
