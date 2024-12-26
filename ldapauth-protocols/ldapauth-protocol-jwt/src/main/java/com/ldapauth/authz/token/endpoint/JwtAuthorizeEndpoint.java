package com.ldapauth.authz.token.endpoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.authz.endpoint.AuthorizeBaseEndpoint;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.constants.ContentType;
import com.ldapauth.crypto.jose.keystore.JWKSetKeyStore;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.pojo.entity.client.Client;
import com.ldapauth.pojo.entity.client.details.ClientJWTDetails;
import org.apache.commons.lang3.StringUtils;
import com.ldapauth.authz.jwt.endpoint.adapter.JwtAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Objects;

/**
 * @author Crystal.Sea
 *
 */
@Tag(name = "2-5-JWT令牌接口")
@Controller
public class JwtAuthorizeEndpoint  extends AuthorizeBaseEndpoint {

	final static Logger _logger = LoggerFactory.getLogger(JwtAuthorizeEndpoint.class);

	@Autowired
	ApplicationConfig applicationConfig;

	@Operation(summary = "JWT应用ID认证接口", description = "应用ID",method="GET")
	@GetMapping("/auth/jwt/{id}")
	public ModelAndView authorize(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("id") String appId){
		ModelAndView modelAndView=new ModelAndView();
		Client apps = getApp(Long.valueOf(appId));
		ClientJWTDetails jwtDetails = appsService.getAppsJwtDetails(Long.valueOf(appId));
		if(Objects.isNull(apps)) {
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "无法查询应用["+appId+"]");
		}
		if(Objects.nonNull(apps) && apps.getStatus().intValue() != 0) {
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "应用["+appId+"]被禁用");
		}
		if(Objects.isNull(jwtDetails)) {
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "无法查询应用["+appId+"]");
		}

		_logger.debug(""+jwtDetails);
		JwtAdapter adapter =new JwtAdapter(jwtDetails);
		adapter.setPrincipal(AuthorizationUtils.getPrincipal());
		//生成JWT内容
		adapter.generateInfo();
		//签名
		adapter.sign(null);
		//加密
		adapter.encrypt(null);
		return adapter.authorize(modelAndView);
	}

	@Operation(summary = "JWT JWK元数据接口", description = "APPID",method="GET")
	@RequestMapping(
			value = "/metadata/jwt/{appid}.{mediaType}",
			method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public String  metadata(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("appid") String appId,
			@PathVariable("mediaType") String mediaType) {
		ClientJWTDetails jwtDetails = appsService.getAppsJwtDetails(Long.valueOf(appId));
		Client apps = getApp(Long.valueOf(appId));
		if(Objects.isNull(apps)) {
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "无法查询应用["+appId+"]");
		}
		if(Objects.nonNull(apps) && apps.getStatus().intValue() != 0) {
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "应用["+appId+"]被禁用");
		}
		if(Objects.isNull(jwtDetails)) {
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "无法查询应用["+appId+"]");
		}

		if(jwtDetails != null) {
			String jwkSetString = "";
			if(!jwtDetails.getSignature().equalsIgnoreCase("none")) {
				jwkSetString = jwtDetails.getSignatureKey();
			}
			JWKSetKeyStore jwkSetKeyStore = new JWKSetKeyStore("{\"keys\": [" + jwkSetString + "]}");
			if(StringUtils.isNotBlank(mediaType)
					&& mediaType.equalsIgnoreCase(ContentType.XML)) {
				response.setContentType(ContentType.APPLICATION_XML_UTF8);
			}else {
				response.setContentType(ContentType.APPLICATION_JSON_UTF8);
			}
			return jwkSetKeyStore.toString(mediaType);

		}
		return appId + " not exist.";
	}
}
