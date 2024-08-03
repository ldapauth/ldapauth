package com.ldapauth.authz.cas.endpoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authz.cas.endpoint.response.Service10ResponseBuilder;
import com.ldapauth.authz.cas.endpoint.ticket.CasConstants;
import com.ldapauth.authz.cas.endpoint.ticket.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Crystal.Sea
 * https://apereo.github.io/cas/6.2.x/protocol/CAS-Protocol-Specification.html
 */
@Tag(name = "2-3-CAS API文档模块")
@Controller
public class Cas10AuthorizeEndpoint   extends CasBaseAuthorizeEndpoint{

	final static Logger _logger = LoggerFactory.getLogger(Cas10AuthorizeEndpoint.class);

	@Operation(summary = "CAS 1.0 ticket验证接口", description = "通过ticket获取当前登录用户信息",method="POST")
	@RequestMapping(CasConstants.ENDPOINT.ENDPOINT_VALIDATE)
	@ResponseBody
	public String validate(
			@RequestParam(value = CasConstants.PARAMETER.TICKET) String ticket,
			@RequestParam(value = CasConstants.PARAMETER.SERVICE) String service,
			@RequestParam(value = CasConstants.PARAMETER.RENEW,required=false) String renew
			 ){
	    _logger.debug("serviceValidate "
                + " ticket " + ticket
                +" , service " + service
                +" , renew " + renew
        );

		Ticket storedTicket = null;
		try {
			storedTicket = ticketServices.consumeTicket(ticket);
		} catch (Exception e) {
			_logger.error("consume Ticket error " , e);
		}
		if(storedTicket != null){
			String principal= ((SignPrincipal) storedTicket.getAuthentication().getPrincipal()).getUsername();
			_logger.debug("principal "+principal);
			return new Service10ResponseBuilder().success()
					.setUser(principal)
					.serviceResponseBuilder();
		}else{
		    _logger.debug("Ticket not found .");
			return new Service10ResponseBuilder().failure()
					.serviceResponseBuilder();
		}
	}
}
