package com.ldapauth.authn.web.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.persistence.service.PermissionAuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;


/**
 * 登录认证判断
 *
 * @author Crystal.Sea
 *
 */
@Component
public class PermissionInterceptor  implements AsyncHandlerInterceptor  {
	private static final Logger _logger = LoggerFactory.getLogger(PermissionInterceptor.class);
	/**
	 * 系统配置
	 */
	@Autowired
	ApplicationConfig applicationConfig;
	/**
	 * 会话管理
	 */
	@Autowired
	SessionManager sessionManager;
	/**
	 * 认证令牌服务
	 */
	@Autowired
	AuthTokenService authTokenService;

	/**
	 * 鉴权
	 */
	@Autowired
	PermissionAuthenticationService permissionAuthenticationService;

	/**
	 * 是否管理端
	 */
	boolean mgmt = false;

	/*
	 * 请求前处理
	 *  (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		 _logger.trace("Permission Interceptor .");
		 //认证信息识别
		 AuthorizationUtils.authenticate(request, authTokenService, sessionManager);
		 SignPrincipal principal = AuthorizationUtils.getPrincipal();//读取认证当事人
		//判断用户是否登录,判断用户是否登录用户
		if (principal == null){
			_logger.trace("No Authentication ... forward to /auth/entrypoint , request URI " + request.getRequestURI());
			RequestDispatcher dispatcher = request.getRequestDispatcher("/auth/entrypoint");
		    dispatcher.forward(request, response);
		    return false;
		}
		//如果是超级管理员直接放行
		if (principal.isRoleAdministrators()) {
			return true;
		}
		//鉴权API
		String path = request.getRequestURI();

		if (!permissionAuthenticationService.authApi(principal.getUserInfo().getId(),path)) {
			//没有权限，则进行拦截
			_logger.trace("No Authentication ... forward to /auth/entrypoint , request URI " + request.getRequestURI());
			RequestDispatcher dispatcher = request.getRequestDispatcher("/auth/entrypoint");
			dispatcher.forward(request, response);
			return false;
		}
		return true;
	}



	/**
	 * 设置管理
	 * @param mgmt
	 */
	public void setMgmt(boolean mgmt) {
		this.mgmt = mgmt;
		_logger.debug("Permission for ADMINISTRATORS {}", this.mgmt);
	}

}
