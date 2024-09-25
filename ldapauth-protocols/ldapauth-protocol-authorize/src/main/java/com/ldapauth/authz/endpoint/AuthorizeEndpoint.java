package com.ldapauth.authz.endpoint;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import com.ldapauth.exception.BusinessException;
import com.ldapauth.pojo.entity.apps.ClientApps;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Crystal.Sea
 *
 */
@Tag(name = "1-2认证总地址文档模块")
@Controller
public class AuthorizeEndpoint extends AuthorizeBaseEndpoint{
	final static Logger _logger = LoggerFactory.getLogger(AuthorizeEndpoint.class);

	//all single sign on url
	@Operation(summary = "认证总地址接口", description = "参数应用ID，分发到不同应用的认证地址",method="GET")
	@GetMapping("/auth/{id}")
	public ModelAndView authorize(@Validated @NotNull(message = "ID不能为空") @PathVariable("id") String id){
		ModelAndView modelAndView = null;
		ClientApps app=getApp(Long.valueOf(id));
		WebContext.setAttribute(WebConstants.SINGLE_SIGN_ON_APP_ID, app.getId());
		//0=oidc 1=saml 2=jwt 3=cas
		switch (app.getProtocol().intValue()) {
			case 0:
				modelAndView=WebContext.forward("/auth/oauth/v20/"+app.getId());
				break;
			case 1:
				modelAndView=WebContext.forward("/auth/saml20/idpinit/"+app.getId());
				break;
			case 2:
				modelAndView=WebContext.forward("/auth/jwt/"+app.getId());
				break;
			case 3:
				modelAndView=WebContext.forward("/auth/cas/"+app.getId());
				break;
			default:
				throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "无法识别应用协议");
		}
		_logger.debug(modelAndView.getViewName());
		return modelAndView;
	}


	@GetMapping("/auth/refused")
	public ModelAndView refused(){
		ModelAndView modelAndView = new ModelAndView("authorize/authorize_refused");
		ClientApps app = (ClientApps)WebContext.getAttribute(WebConstants.AUTHORIZE_SIGN_ON_APP);
		modelAndView.addObject("model", app);
		return modelAndView;
	}

	/**
	 *
	 * @param request
	 * @param receiver flutter/desktop/android/ios
	 * @return View
	 */
	@GetMapping("/auth/redirect/{receiver}")
	public ModelAndView redirect(HttpServletRequest request,@PathVariable("receiver") String receiver){
		ModelAndView modelAndView = new ModelAndView("authorize/authorize_redirect_receiver");
		HashMap<String,String> paramenter = new HashMap<String,String>();
		paramenter.put("receiver", receiver);
		paramenter.put("code", request.getParameter("code"));
		paramenter.put("state", request.getParameter("state"));
		modelAndView.addObject("model", paramenter);
		return modelAndView;
	}

}
