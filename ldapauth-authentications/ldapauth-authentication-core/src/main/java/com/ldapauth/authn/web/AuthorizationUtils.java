package com.ldapauth.authn.web;

import java.text.ParseException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.ldapauth.authn.SignPrincipal;
import org.apache.commons.lang3.StringUtils;
import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.authn.session.Session;
import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.util.AuthorizationHeaderUtils;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

/**
 * 认证信息工具类
 *
 * @author Crystal.Sea
 *
 */
public class AuthorizationUtils {
	private static final Logger _logger = LoggerFactory.getLogger(AuthorizationUtils.class);

	public static final class BEARERTYPE{
		/**
		 * cookie name congress
		 */
		public static final String CONGRESS 		= "congress";
		/**
		 * cookie name congress
		 */
		public static final String ADMIN_TOKEN 		= "Admin-Token";


		/**
		 * cookie name congress
		 */
		public static final String TOKEN 		= "token";

		/**
		 * Http header name Authorization
		 */
		public static final String AUTHORIZATION 	= "Authorization";
	}

	/**
	 * authenticate by Cookie/Param/根据congress cookie 认证
	 * @param request
	 * @param authTokenService
	 * @param sessionManager
	 * @throws ParseException
	 */
	public static  void authenticateWithCookie(
			HttpServletRequest request,
			AuthTokenService authTokenService,
			SessionManager sessionManager
			) throws ParseException{
		//基于param
		String authorization = WebContext.getParameter(BEARERTYPE.TOKEN);
		if (StringUtils.isNotEmpty(authorization)) {
			_logger.trace("Try param congress authenticate .");
			doJwtAuthenticate(BEARERTYPE.CONGRESS,authorization,authTokenService,sessionManager);
			return;
		}
		//基于cookie
		Cookie authCookie = WebContext.getCookie(request, BEARERTYPE.CONGRESS);
		if (authCookie != null ) {//cookie不为空，解析cookie并认证
			_logger.trace("Try congress authenticate .");
	    	authorization =  authCookie.getValue();
	    	doJwtAuthenticate(BEARERTYPE.CONGRESS,authorization,authTokenService,sessionManager);
			return;
		}
		//兼容cookie名称
		authCookie = WebContext.getCookie(request, BEARERTYPE.ADMIN_TOKEN);
		if (authCookie != null ) {//cookie不为空，解析cookie并认证
			_logger.trace("Try congress authenticate .");
			authorization =  authCookie.getValue();
			doJwtAuthenticate(BEARERTYPE.ADMIN_TOKEN,authorization,authTokenService,sessionManager);
			return;
		}

		//cookie 为空，需要清除认证信息
		_logger.debug("cookie is null , clear authentication .");
		clearAuthentication();

	}

	/**
	 * 根据前端http header Authorization 认证
	 * @param request
	 * @param authTokenService
	 * @param sessionManager
	 * @throws ParseException
	 */
	public static  void authenticate(
			HttpServletRequest request,
			AuthTokenService authTokenService,
			SessionManager sessionManager
			) throws ParseException{
		String  authorization = AuthorizationHeaderUtils.resolveBearer(request);
		if(authorization != null ) {
			_logger.trace("Try Authorization authenticate .");
			doJwtAuthenticate(BEARERTYPE.AUTHORIZATION,authorization,authTokenService,sessionManager);
		}

	}

	/**
	 * 根据JWT认证
	 * @param bearerType
	 * @param authorization
	 * @param authTokenService
	 * @param sessionManager
	 * @throws ParseException
	 */
	public static void doJwtAuthenticate(
			String  bearerType,
			String  authorization,
			AuthTokenService authTokenService,
			SessionManager sessionManager) throws ParseException {
		//验证JWT令牌的有效性
		if(authTokenService.validateJwtToken(authorization)) {
			//未认证情况
			if(isNotAuthenticated()) {
				//解析jwt jid即会话id
				String sessionId = authTokenService.resolveJWTID(authorization);
				//读取会话
				Session session = sessionManager.get(sessionId);
				if(session != null) {//会话不为空，设置认证信息
					setAuthentication(session.getAuthentication());
					_logger.debug("{} Automatic authenticated .",bearerType);
				}else {
					//time out/会话超时
					_logger.debug("Session timeout .");
					clearAuthentication();
				}
			}
		}else {
			//token invalidate/验证不通过，会话过期，需要清除认证信息
			_logger.debug("Token invalidate .");
			clearAuthentication();
		}
	}

    /**
     * 读取Authentication by WebContext.getRequest
     * @return
     */
    public static Authentication getAuthentication() {
    	Authentication authentication = (Authentication) getAuthentication(WebContext.getRequest());
        return authentication;
    }

    /**
     * HttpServletRequest 中读取认证信息
     * @param request
     * @return
     */
    public static Authentication getAuthentication(HttpServletRequest request) {
    	Authentication authentication = (Authentication) request.getSession().getAttribute(WebConstants.AUTHENTICATION);
        return authentication;
    }

    /**
     * set Authentication to http session/认证会话信息
     * @param authentication
     */
    public static void setAuthentication(Authentication authentication) {
    	WebContext.setAttribute(WebConstants.AUTHENTICATION, authentication);
    }

    /**
     * 清除当前认证
     */
    public static void clearAuthentication() {
    	WebContext.removeAttribute(WebConstants.AUTHENTICATION);
    }

    public static  boolean isAuthenticated() {
    	return getAuthentication() != null;
    }

    public static  boolean isNotAuthenticated() {
    	return ! isAuthenticated();
    }

    public static SignPrincipal getPrincipal() {
    	 Authentication authentication =  getAuthentication();
    	return getPrincipal(authentication);
    }

    public static SignPrincipal getPrincipal(Authentication authentication) {
    	return authentication == null ? null : (SignPrincipal) authentication.getPrincipal();
   }

    /**
     * 读取认证的用户信息
     * @param authentication
     * @return UserInfo
     */
    public static UserInfo getUserInfo(Authentication authentication) {
    	UserInfo userInfo = null;
    	SignPrincipal principal = getPrincipal(authentication);
    	if(principal != null ) {
        	userInfo = principal.getUserInfo();
        }
    	return userInfo;
    }

    public static UserInfo getUserInfo() {
    	return getUserInfo(getAuthentication());
    }

}
