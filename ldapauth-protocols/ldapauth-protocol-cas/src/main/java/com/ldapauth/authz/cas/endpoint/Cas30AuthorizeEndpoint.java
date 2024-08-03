package com.ldapauth.authz.cas.endpoint;

import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authz.cas.endpoint.adapter.CasPlainAdapter;
import com.ldapauth.authz.endpoint.adapter.AbstractAuthorizeAdapter;
import com.ldapauth.constants.ConstsBoolean;
import com.ldapauth.util.Instance;
import com.ldapauth.web.HttpResponseConstants;
import org.apache.commons.beanutils.BeanUtils;
import com.ldapauth.authz.cas.endpoint.response.ProxyServiceResponseBuilder;
import com.ldapauth.authz.cas.endpoint.response.ServiceResponseBuilder;
import com.ldapauth.authz.cas.endpoint.ticket.CasConstants;
import com.ldapauth.authz.cas.endpoint.ticket.ProxyGrantingTicketIOUImpl;
import com.ldapauth.authz.cas.endpoint.ticket.ProxyGrantingTicketImpl;
import com.ldapauth.authz.cas.endpoint.ticket.ProxyTicketImpl;
import com.ldapauth.authz.cas.endpoint.ticket.Ticket;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "2-3-CAS API文档模块")
@Controller
public class Cas30AuthorizeEndpoint  extends CasBaseAuthorizeEndpoint{

	final static Logger _logger = LoggerFactory.getLogger(Cas30AuthorizeEndpoint.class);

	@Operation(summary = "CAS 3.0 ticket验证接口", description = "通过ticket获取当前登录用户信息",method="POST")
	@RequestMapping(value= CasConstants.ENDPOINT.ENDPOINT_SERVICE_VALIDATE_V3)
	public void serviceValidate(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = CasConstants.PARAMETER.TICKET) String ticket,
			@RequestParam(value = CasConstants.PARAMETER.SERVICE) String service,
			@RequestParam(value = CasConstants.PARAMETER.PROXY_CALLBACK_URL,required=false) String pgtUrl,
			@RequestParam(value = CasConstants.PARAMETER.RENEW,required=false) String renew,
			@RequestParam(value = CasConstants.PARAMETER.FORMAT,required=false,defaultValue= HttpResponseConstants.FORMAT_TYPE.XML) String format){
	    _logger.debug("serviceValidate "
	                    + " ticket " + ticket
	                    +" , service " + service
	                    +" , pgtUrl " + pgtUrl
	                    +" , renew " + renew
	                    +" , format " + format
	            );

		Ticket storedTicket=null;
		if(ticket.startsWith(CasConstants.PREFIX.SERVICE_TICKET_PREFIX)) {
			try {
				storedTicket = ticketServices.consumeTicket(ticket);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ServiceResponseBuilder serviceResponseBuilder=new ServiceResponseBuilder();

		if(storedTicket!=null){
		    SignPrincipal authentication = ((SignPrincipal)storedTicket.getAuthentication().getPrincipal());
			if(StringUtils.isNotBlank(pgtUrl)) {
				ProxyGrantingTicketIOUImpl proxyGrantingTicketIOUImpl =new ProxyGrantingTicketIOUImpl();
				String proxyGrantingTicketIOU=casProxyGrantingTicketServices.createTicket(proxyGrantingTicketIOUImpl);

				ProxyGrantingTicketImpl proxyGrantingTicketImpl=new ProxyGrantingTicketImpl(storedTicket.getAuthentication(),storedTicket.getCasDetails());
				String proxyGrantingTicket=casProxyGrantingTicketServices.createTicket(proxyGrantingTicketImpl);

				serviceResponseBuilder.success().setTicket(proxyGrantingTicketIOU);
				serviceResponseBuilder.success().setProxy(pgtUrl);

				httpRequestAdapter.post(pgtUrl+"?pgtId="+proxyGrantingTicket+"&pgtIou="+proxyGrantingTicketIOU,null);
			}
			CasPlainAdapter adapter = new CasPlainAdapter(storedTicket.getCasDetails());
			adapter.setServiceResponseBuilder(serviceResponseBuilder);
			adapter.setPrincipal(authentication);
			adapter.generateInfo();
		}else{
			serviceResponseBuilder.failure()
				.setCode(CasConstants.ERROR_CODE.INVALID_TICKET)
				.setDescription("Ticket "+ticket+" not recognized");
		}

		httpResponseAdapter.write(response,serviceResponseBuilder.serviceResponseBuilder(),format);
	}

	@Operation(summary = "CAS 3.0 ProxyTicket代理验证接口", description = "通过ProxyGrantingTicket获取ProxyTicket",method="POST")
	@RequestMapping(CasConstants.ENDPOINT.ENDPOINT_PROXY_V3)
	public void proxy(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = CasConstants.PARAMETER.PROXY_GRANTING_TICKET) String pgt,
			@RequestParam(value = CasConstants.PARAMETER.TARGET_SERVICE) String targetService,
			@RequestParam(value = CasConstants.PARAMETER.FORMAT,required=false,defaultValue=HttpResponseConstants.FORMAT_TYPE.XML) String format){
	    _logger.debug("proxy "
                + " pgt " + pgt
                +" , targetService " + targetService
                +" , format " + format
        );
	    ProxyServiceResponseBuilder proxyServiceResponseBuilder=new ProxyServiceResponseBuilder();
	    ProxyGrantingTicketImpl proxyGrantingTicketImpl = (ProxyGrantingTicketImpl)casProxyGrantingTicketServices.get(pgt);
	    if(proxyGrantingTicketImpl != null) {
	    	ProxyTicketImpl ProxyTicketImpl = new ProxyTicketImpl(proxyGrantingTicketImpl.getAuthentication(),proxyGrantingTicketImpl.getCasDetails());
	    	String proxyTicket =ticketServices.createTicket(ProxyTicketImpl);
	 		proxyServiceResponseBuilder.success().setTicket(proxyTicket).setFormat(format);
	    }else {
	    	proxyServiceResponseBuilder.success().setTicket("").setFormat(format);
	    }

	    httpResponseAdapter.write(response,proxyServiceResponseBuilder.serviceResponseBuilder(),format);
	}

	@Operation(summary = "CAS 3.0 ticket代理验证接口", description = "通过ProxyTicket获取当前登录用户信息",method="POST")
	@RequestMapping(CasConstants.ENDPOINT.ENDPOINT_PROXY_VALIDATE_V3)
	public void proxy(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = CasConstants.PARAMETER.TICKET) String ticket,
			@RequestParam(value = CasConstants.PARAMETER.SERVICE) String service,
			@RequestParam(value = CasConstants.PARAMETER.PROXY_CALLBACK_URL,required=false) String pgtUrl,
			@RequestParam(value = CasConstants.PARAMETER.RENEW,required=false) String renew,
			@RequestParam(value = CasConstants.PARAMETER.FORMAT,required=false,defaultValue=HttpResponseConstants.FORMAT_TYPE.XML) String format){
	    _logger.debug("proxyValidate "
                + " ticket " + ticket
                +" , service " + service
                +" , pgtUrl " + pgtUrl
                +" , renew " + renew
                +" , format " + format
        );

		Ticket storedTicket=null;
		if (ticket.startsWith(CasConstants.PREFIX.PROXY_TICKET_PREFIX)) {
			try {
					storedTicket = ticketServices.consumeTicket(ticket);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ServiceResponseBuilder serviceResponseBuilder=new ServiceResponseBuilder();

		if (storedTicket!=null){
			SignPrincipal authentication = ((SignPrincipal)storedTicket.getAuthentication().getPrincipal());
			CasPlainAdapter adapter = new CasPlainAdapter(storedTicket.getCasDetails());
			adapter.setPrincipal(authentication);
			adapter.setServiceResponseBuilder(serviceResponseBuilder);
			adapter.generateInfo();
		}else{
			serviceResponseBuilder.failure()
				.setCode(CasConstants.ERROR_CODE.INVALID_TICKET)
				.setDescription("Ticket "+ticket+" not recognized");
		}
		httpResponseAdapter.write(response,serviceResponseBuilder.serviceResponseBuilder(),format);
	}
}
