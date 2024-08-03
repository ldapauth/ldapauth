package com.ldapauth.authz.cas.endpoint;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authz.cas.endpoint.adapter.CasPlainAdapter;
import com.ldapauth.authz.cas.endpoint.response.ProxyServiceResponseBuilder;
import com.ldapauth.authz.cas.endpoint.response.ServiceResponseBuilder;
import com.ldapauth.web.HttpResponseConstants;
import com.ldapauth.authz.cas.endpoint.ticket.CasConstants;
import com.ldapauth.authz.cas.endpoint.ticket.ProxyGrantingTicketIOUImpl;
import com.ldapauth.authz.cas.endpoint.ticket.ProxyGrantingTicketImpl;
import com.ldapauth.authz.cas.endpoint.ticket.ProxyTicketImpl;
import com.ldapauth.authz.cas.endpoint.ticket.Ticket;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Crystal.Sea
 * https://apereo.github.io/cas/6.2.x/protocol/CAS-Protocol-V2-Specification.html
 */
@Tag(name = "2-3-CAS API文档模块")
@Controller
public class Cas20AuthorizeEndpoint  extends CasBaseAuthorizeEndpoint{

	final static Logger _logger = LoggerFactory.getLogger(Cas20AuthorizeEndpoint.class);

	@Operation(summary = "CAS 2.0 ticket验证接口", description = "通过ticket获取当前登录用户信息",method="POST")
	@RequestMapping(value= CasConstants.ENDPOINT.ENDPOINT_SERVICE_VALIDATE,produces =MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public String serviceValidate(
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
			adapter.setPrincipal(authentication);
			adapter.setServiceResponseBuilder(serviceResponseBuilder);
			adapter.generateInfo();
		}else{
			serviceResponseBuilder.failure()
				.setCode(CasConstants.ERROR_CODE.INVALID_TICKET)
				.setDescription("Ticket "+ticket+" not recognized");
		}

		return serviceResponseBuilder.serviceResponseBuilder();
	}

	/**
	 * @param request
	 * @param response
	 * @param ticket
	 * @param service
	 * @param pgtUrl
	 * @param renew
	 * @return
2.6. /proxyValidate [CAS 2.0]
/proxyValidate MUST perform the same validation tasks as /serviceValidate and additionally validate proxy tickets. /proxyValidate MUST be capable of validating both service tickets and proxy tickets. See Section 2.5.4 for details.


2.6.1. parameters
/proxyValidate has the same parameter requirements as /serviceValidate. See Section 2.5.1.


2.6.2. response
/proxyValidate will return an XML-formatted CAS serviceResponse as described in the XML schema in Appendix A. Below are example responses:

Response on ticket validation success:
  <cas:serviceResponse xmlns:cas="http://www.yale.edu/tp/cas">
    <cas:authenticationSuccess>
      <cas:user>username</cas:user>
      <cas:proxyGrantingTicket>PGTIOU-84678-8a9d...</cas:proxyGrantingTicket>
      <cas:proxies>
        <cas:proxy>https://proxy2/pgtUrl</cas:proxy>
        <cas:proxy>https://proxy1/pgtUrl</cas:proxy>
      </cas:proxies>
    </cas:authenticationSuccess>
  </cas:serviceResponse>

{
  "serviceResponse" : {
    "authenticationSuccess" : {
      "user" : "username",
      "proxyGrantingTicket" : "PGTIOU-84678-8a9d...",
      "proxies" : [ "https://proxy1/pgtUrl", "https://proxy2/pgtUrl" ]
    }
  }
}

Note: when authentication has proceeded through multiple proxies, the order in which the proxies were traversed MUST be reflected in the <cas:proxies> block. The most recently-visited proxy MUST be the first proxy listed, and all the other proxies MUST be shifted down as new proxies are added. In the above example, the service identified by <https://proxy1/pgtUrl> was visited first, and that service proxied authentication to the service identified by <https://proxy2/pgtUrl>.

Response on ticket validation failure:

  <cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>
      <cas:authenticationFailure code="INVALID_TICKET">
         ticket PT-1856376-1HMgO86Z2ZKeByc5XdYD not recognized
      </cas:authenticationFailure>
  </cas:serviceResponse>

{
  "serviceResponse" : {
    "authenticationFailure" : {
      "code" : "INVALID_TICKET",
      "description" : "Ticket PT-1856339-aA5Yuvrxzpv8Tau1cYQ7 not recognized"
    }
  }
}
	 */

	@Operation(summary = "CAS 2.0 ticket代理验证接口", description = "通过ticket获取当前登录用户信息",method="POST")
	@RequestMapping(value=CasConstants.ENDPOINT.ENDPOINT_PROXY_VALIDATE,produces =MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public String proxy(
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
		if (Objects.nonNull(storedTicket)){
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
		return serviceResponseBuilder.serviceResponseBuilder();
	}

	/**
	 * @param request
	 * @param response
	 * @param pgt
	 * @param targetService
	 * @return
2.7. /proxy [CAS 2.0]
/proxy provides proxy tickets to services that have acquired proxy-granting tickets and will be proxying authentication to back-end services.


2.7.1. parameters
The following HTTP request parameters MUST be specified to /proxy. They are both case-sensitive.

pgt [REQUIRED] - the proxy-granting ticket acquired by the service during service ticket or proxy ticket validation.
targetService [REQUIRED] - the service identifier of the back-end service. Note that not all back-end services are web services so this service identifier will not always be an URL. However, the service identifier specified here MUST match the service parameter specified to /proxyValidate upon validation of the proxy ticket.

2.7.2. response
/proxy will return an XML-formatted CAS serviceResponse document as described in the XML schema in Appendix A. Below are example responses:

Response on request success:
  <cas:serviceResponse xmlns:cas="http://www.yale.edu/tp/cas">
      <cas:proxySuccess>
          <cas:proxyTicket>PT-1856392-b98xZrQN4p90ASrw96c8</cas:proxyTicket>
      </cas:proxySuccess>
  </cas:serviceResponse>

Response on request failure:
<cas:serviceResponse xmlns:cas="http://www.yale.edu/tp/cas">
      <cas:proxyFailure code="INVALID_REQUEST">
          'pgt' and 'targetService' parameters are both required
      </cas:proxyFailure>
  </cas:serviceResponse>
{
  "serviceResponse" : {
    "authenticationFailure" : {
      "code" : "INVALID_REQUEST",
      "description" : "'pgt' and 'targetService' parameters are both required"
    }
  }
}


2.7.3. error codes
The following values MAY be used as the code attribute of authentication failure responses. The following is the minimum set of error codes that all CAS servers MUST implement. Implementations MAY include others.

INVALID_REQUEST - not all of the required request parameters were present

UNAUTHORIZED_SERVICE - service is unauthorized to perform the proxy request

INTERNAL_ERROR - an internal error occurred during ticket validation

For all error codes, it is RECOMMENDED that CAS provide a more detailed message as the body of the <cas:authenticationFailure> block of the XML response.
	 */
	@RequestMapping(value=CasConstants.ENDPOINT.ENDPOINT_PROXY ,produces =MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public String proxy(
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
		if (Objects.nonNull(proxyGrantingTicketImpl)){
	    	ProxyTicketImpl ProxyTicketImpl = new ProxyTicketImpl(proxyGrantingTicketImpl.getAuthentication(),proxyGrantingTicketImpl.getCasDetails());
	    	String proxyTicket =ticketServices.createTicket(ProxyTicketImpl);
	 		proxyServiceResponseBuilder.success().setTicket(proxyTicket).setFormat(format);
	    }else {
	    	proxyServiceResponseBuilder.success().setTicket("").setFormat(format);
	    }
		return proxyServiceResponseBuilder.serviceResponseBuilder();
	}
}
