package com.ldapauth.authz.cas.endpoint;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.authz.cas.endpoint.ticket.CasConstants;
import com.ldapauth.authz.cas.endpoint.ticket.ServiceTicketImpl;
import com.ldapauth.cache.CacheService;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.pojo.dto.AppsDetails;
import com.ldapauth.pojo.entity.apps.details.ClientAppsCASDetails;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class CasAuthorizeEndpoint  extends CasBaseAuthorizeEndpoint{

	final static Logger _logger = LoggerFactory.getLogger(CasAuthorizeEndpoint.class);

	@Autowired
	CacheService cacheService;

	@Autowired
	IdentifierGenerator identifierGenerator;

	static final String CAS_SSO_CODE = "CAS:SSO:CODE";


	@Operation(summary = "CAS页面跳转service认证接口", description = "传递参数service",method="GET")
	@GetMapping(CasConstants.ENDPOINT.ENDPOINT_LOGIN)
	public ModelAndView casLogin(@RequestParam(value=CasConstants.PARAMETER.SERVICE,required=false) String casService,
								 HttpServletRequest request,
								 HttpServletResponse response
			){
		ClientAppsCASDetails casDetails = appsService.getAppDetails(casService , true);
		if (Objects.nonNull(casDetails)) {
			Result<AppsDetails> casDetails3 = appsService.getDetails(casDetails.getAppId());
			if (casDetails3.isSuccess()) {
				if (casDetails3.getData().getApp().getStatus() != 0) {
					throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "["+casService+"]关联的应用被禁用");
				}

				return buildCasModelAndView(request, response, casDetails3.getData(), casService);
			}
		}
		throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "无法查询应用["+casService+"]");
	}

	@Operation(summary = "CAS页面跳转应用ID认证接口", description = "传递参数应用ID",method="GET")
	@GetMapping(CasConstants.ENDPOINT.ENDPOINT_BASE + "/{id}")
	public ModelAndView authorize(  @PathVariable("id") String id,
									HttpServletRequest request,
									HttpServletResponse response){
		Result<AppsDetails> casDetails = appsService.getDetails(Long.valueOf(id));
		if (casDetails.isSuccess()) {
			if (casDetails.getData().getApp().getStatus() != 0) {
				throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "应用["+id+"]被禁用");
			}
			AppsDetails details = casDetails.getData();
			ClientAppsCASDetails appsCasDetails = (ClientAppsCASDetails) details.getDetails();
			return buildCasModelAndView(request, response, details, casDetails == null ? id : appsCasDetails.getServerNames());
		}
		throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "发起失败,无法查找应用标识");
	}

	private  ModelAndView buildCasModelAndView( HttpServletRequest request,
	                							HttpServletResponse response,
												AppsDetails casDetails,
	                							String casService){
		if (casDetails == null) {
			_logger.debug("service {} not registered  " , casService);
			ModelAndView modelAndView = new ModelAndView("authorize/cas_sso_submint");
			modelAndView.addObject("errorMessage", casService);
			return modelAndView;
		}
		_logger.debug("Detail {}" , casDetails);
		Map<String, String> parameterMap = WebContext.getRequestParameterMap(request);
		String service = casService;
		_logger.debug("CAS Parameter service = {}" , service);
		if(casService.indexOf("?") >-1 ) {
		    service = casService.substring(casService.indexOf("?") + 1);
		    if(service.indexOf("=") > -1) {
		        String [] parameterValues = service.split("=");
		        if(parameterValues.length == 2) {
		            parameterMap.put(parameterValues[0], parameterValues[1]);
		        }
		    }
		    _logger.debug("CAS service with Parameter : {}" , parameterMap);
		}
		ClientAppsCASDetails appsCasDetails = (ClientAppsCASDetails) casDetails.getDetails();
		appsCasDetails.setServerNames(casService);
		String tempCode = identifierGenerator.nextUUID(CAS_SSO_CODE);
		//缓存1分钟
		cacheService.setCacheObject(CAS_SSO_CODE+tempCode,appsCasDetails,1, TimeUnit.MINUTES);
		WebContext.setAttribute(CasConstants.PARAMETER.PARAMETER_MAP, parameterMap);
		WebContext.setAttribute(CasConstants.PARAMETER.ENDPOINT_CAS_DETAILS, casDetails);
		WebContext.setAttribute(WebConstants.SINGLE_SIGN_ON_APP_ID, casDetails.getApp().getId());
		WebContext.setAttribute(WebConstants.AUTHORIZE_SIGN_ON_APP,casDetails.getApp());
		return WebContext.redirect(CasConstants.ENDPOINT.ENDPOINT_SERVICE_TICKET_GRANTING+"?code="+tempCode);
	}

	@RequestMapping(CasConstants.ENDPOINT.ENDPOINT_SERVICE_TICKET_GRANTING)
	public ModelAndView grantingTicket(){

		ModelAndView modelAndView = new ModelAndView("authorize/cas_sso_submint");
		String code = WebContext.getParameter("code");
		String tempCode = CAS_SSO_CODE + code;
		ClientAppsCASDetails appsCasDetails = cacheService.getCacheObject(tempCode);
		if (Objects.isNull(appsCasDetails)) {
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"无法识别临时码["+code+"]");
		}
		ServiceTicketImpl serviceTicket = new ServiceTicketImpl(AuthorizationUtils.getAuthentication(),appsCasDetails);

		String ticket = ticketServices.createTicket(serviceTicket,appsCasDetails.getTicketValidity().intValue());
		_logger.trace("CAS ticket {} created for APP {} . " , ticket , appsCasDetails.getId());

		//判断是否存在分号，如果存在则取第一条回调地址
		if (appsCasDetails.getServerNames().indexOf(";") != -1) {
			appsCasDetails.setServerNames(StringUtils.split(";")[0]);
		}

		StringBuffer callbackUrl = new StringBuffer(appsCasDetails.getServerNames());

		if(appsCasDetails.getServerNames().indexOf("?")==-1) {
			callbackUrl.append("?");
		}
		if(callbackUrl.indexOf("&") != -1 ||callbackUrl.indexOf("=") != -1) {
			callbackUrl.append("&");
		}
		//append ticket
		callbackUrl.append(CasConstants.PARAMETER.TICKET).append("=").append(ticket);

		callbackUrl.append("&");
		//append service
		callbackUrl.append(CasConstants.PARAMETER.SERVICE).append("=").append(appsCasDetails.getServerNames());

		//增加可自定义的参数
		if(WebContext.getAttribute(CasConstants.PARAMETER.PARAMETER_MAP)!=null) {
			@SuppressWarnings("unchecked")
			Map <String, String> parameterMap = (Map <String, String>)WebContext.getAttribute(CasConstants.PARAMETER.PARAMETER_MAP);
			parameterMap.remove(CasConstants.PARAMETER.TICKET);
			parameterMap.remove(CasConstants.PARAMETER.SERVICE);
			for (String key : parameterMap.keySet()) {
				callbackUrl.append("&").append(key).append("=").append(parameterMap.get(key));
			}
		}
		_logger.debug("redirect to CAS Client URL {}" , callbackUrl);
		modelAndView.addObject("callbackUrl", callbackUrl.toString());
		return modelAndView;
	}
}
