package com.ldapauth.openapi.rest.interceptor;

import com.alibaba.fastjson.JSON;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;
import com.ldapauth.authz.oauth2.provider.token.DefaultTokenServices;
import com.ldapauth.util.AuthorizationHeader;
import com.ldapauth.util.AuthorizationHeaderUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * basic认证Interceptor处理.
 * @author Shibl
 *
 */
@Component
public class RestApiPermissionAdapter implements AsyncHandlerInterceptor  {
	private static final Logger logger = LoggerFactory.getLogger(RestApiPermissionAdapter.class);

	@Autowired
	DefaultTokenServices oauth20TokenServices;

	@Autowired
	@Qualifier("restProviderManager")
	ProviderManager providerManager;


	/*
	 * 请求前处理
	 *  (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		logger.trace("Rest API Permission Adapter pre handle");
		 AuthorizationHeader headerCredential = AuthorizationHeaderUtils.resolve(request);

		//判断应用的AppId和Secret
		if (headerCredential != null){
			UsernamePasswordAuthenticationToken authenticationToken = null;
			if(headerCredential.isBasic()) {
			    if(StringUtils.isNotBlank(headerCredential.getUsername())&&
			    		StringUtils.isNotBlank(headerCredential.getCredential())
			    		) {
			    	UsernamePasswordAuthenticationToken authRequest =
							new UsernamePasswordAuthenticationToken(
									headerCredential.getUsername(),
									headerCredential.getCredential());
			    	authenticationToken= (UsernamePasswordAuthenticationToken) providerManager.authenticate(authRequest);
			    }
			}else if(StringUtils.isNotBlank(headerCredential.getCredential())){
				logger.trace("Authentication bearer {}" , headerCredential.getCredential());
				OAuth2Authentication oauth2Authentication =
						oauth20TokenServices.loadAuthentication(headerCredential.getCredential());

				if(oauth2Authentication != null) {
					logger.trace("Authentication token {}" , oauth2Authentication.getPrincipal().toString());
					authenticationToken= new UsernamePasswordAuthenticationToken(
			    			new User(
			    					oauth2Authentication.getPrincipal().toString(),
			    					"CLIENT_SECRET",
			    					oauth2Authentication.getAuthorities()),
	                        "PASSWORD",
	                        oauth2Authentication.getAuthorities()
	                );
				}else {
					logger.trace("Authentication token is null ");
				}
			}

			if(authenticationToken !=null && authenticationToken.isAuthenticated()) {
				AuthorizationUtils.setAuthentication(authenticationToken);
				return true;
			}
		}
		notAuth(response);
		return false;
	}

	/**
	 * 返回格式化后的json到前端页面
	 *
	 * @param response
	 * @throws Exception
	 */
	private void notAuth(HttpServletResponse response) {
		PrintWriter writer = null;
		// 注意点1：这边返回配置为josn格式
		response.setContentType("application/json;charset=UTF-8");
		try {
			Map<String, Object> result = new HashMap<>();
			result.put("code", 400);
			result.put("success", false);
			result.put("message", "Not Authorization");
			writer = response.getWriter();
			// 注意点2，这样要用fastjson转换一下后，返回的前端才是格式化后的json格式
			writer.print(JSON.toJSONString(result));
		} catch (IOException e) {

		} finally {

		}
	}
}
