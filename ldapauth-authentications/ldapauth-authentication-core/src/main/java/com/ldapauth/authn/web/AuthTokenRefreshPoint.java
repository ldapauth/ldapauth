


package com.ldapauth.authn.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import com.ldapauth.authn.jwt.AuthJwt;
import com.ldapauth.authn.jwt.AuthRefreshTokenService;
import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.authn.session.Session;
import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.entity.Message;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 前端认证令牌刷新/auth/token/refresh
 *
 * @author Crystal.Sea
 *
 */
@Controller
@RequestMapping(value = "/auth")
public class AuthTokenRefreshPoint {
	private static final  Logger _logger = LoggerFactory.getLogger(AuthTokenRefreshPoint.class);
	/**
	 * 认证令牌服务
	 */
	@Autowired
	AuthTokenService authTokenService;
	/**
	 * 刷新令牌服务
	 */
	@Autowired
	AuthRefreshTokenService refreshTokenService;
	/**
	 * 会话管理
	 */
	@Autowired
	SessionManager sessionManager;

 	/**
 	 * 根据refreshToken刷新认证令牌
 	 * @param request
 	 * @param refreshToken
 	 * @return
 	 */
 	@RequestMapping(value={"/token/refresh"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> refresh(HttpServletRequest request,
			@RequestParam(name = "refresh_token", required = false) String refreshToken) {
 		_logger.debug("try to refresh token " );
 		_logger.trace("refresh token {} " , refreshToken);
 		//日志打印trace
 		if(_logger.isTraceEnabled()) {WebContext.printRequest(request);}
 		try {
 			//判断refreshToken的有效性
	 		if(StringUtils.isNotBlank(refreshToken)
	 				&& refreshTokenService.validateJwtToken(refreshToken)) {
	 			//解析refreshToken，转换会话id
	 			String sessionId = refreshTokenService.resolveJWTID(refreshToken);
	 			//尝试刷新会话
	 			_logger.trace("Try to  refresh sessionId [{}]" , sessionId);
		 		Session session = sessionManager.refresh(sessionId);
		 		if(session != null) {//有会话
		 			//重新生成新令牌
		 			AuthJwt authJwt = authTokenService.genAuthJwt(session.getAuthentication());
		 			_logger.trace("Grant new token {}" , authJwt);
		 			return new Message<AuthJwt>(authJwt).buildResponse();
		 		}else {//无会话
		 			_logger.debug("Session is timeout , sessionId [{}]" , sessionId);
		 		}
	 		}else {//验证失效
	 			_logger.debug("refresh token is not validate .");
	 		}
 		}catch(Exception e) {
 			_logger.error("Refresh Exception !",e);
 		}
 		return new ResponseEntity<>("Refresh Token Fail !", HttpStatus.UNAUTHORIZED);
 	}
}
