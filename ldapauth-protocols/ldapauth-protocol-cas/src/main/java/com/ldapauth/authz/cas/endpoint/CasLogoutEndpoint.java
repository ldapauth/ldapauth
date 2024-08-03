package com.ldapauth.authz.cas.endpoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ldapauth.authz.cas.endpoint.ticket.CasConstants;
import com.ldapauth.web.WebContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Crystal.Sea
 * https://apereo.github.io/cas/6.2.x/protocol/CAS-Protocol.html
 */
@Tag(name = "2-3-CAS API文档模块")
@Controller
public class CasLogoutEndpoint  extends CasBaseAuthorizeEndpoint{

	final static Logger _logger = LoggerFactory.getLogger(CasLogoutEndpoint.class);

	/**
	 * for cas logout then redirect to logout
	 * @param request
	 * @param response
	 * @param casService
	 * @return
	 */
	@Operation(summary = "CAS注销接口", description = "CAS注销接口",method="GET")
	@RequestMapping(CasConstants.ENDPOINT.ENDPOINT_LOGOUT)
	public ModelAndView logout(HttpServletRequest request , HttpServletResponse response,
			@RequestParam(value = CasConstants.PARAMETER.SERVICE , required = false) String casService){
		StringBuffer logoutUrl = new StringBuffer("/force/logout");
		if(StringUtils.isNotBlank(casService)){
			logoutUrl.append("?").append("redirect_uri=").append(casService);
		}
		return WebContext.redirect(logoutUrl.toString());
	}
}
