package com.ldapauth.authz.cas.endpoint;

import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.authz.cas.endpoint.ticket.TicketServices;
import com.ldapauth.authz.endpoint.AuthorizeBaseEndpoint;
import com.ldapauth.persistence.service.AppsService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.web.HttpRequestAdapter;
import com.ldapauth.web.HttpResponseAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CasBaseAuthorizeEndpoint  extends AuthorizeBaseEndpoint {

    final static Logger _logger = LoggerFactory.getLogger(CasBaseAuthorizeEndpoint.class);

    @Autowired
    protected AppsService appsService;

    @Autowired
    protected UserInfoService userInfoService;

    @Autowired
    @Qualifier("casTicketServices")
    protected TicketServices ticketServices;

    @Autowired
    @Qualifier("casTicketGrantingTicketServices")
    protected TicketServices casTicketGrantingTicketServices;

    @Autowired
    protected SessionManager sessionManager;

    @Autowired
    @Qualifier("casProxyGrantingTicketServices")
    protected TicketServices casProxyGrantingTicketServices;

    @Autowired
    protected HttpResponseAdapter httpResponseAdapter;

    @Autowired
    protected HttpRequestAdapter httpRequestAdapter;

}
