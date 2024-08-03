package com.ldapauth.web.contorller;

import com.ldapauth.authn.jwt.AuthJwt;
import com.ldapauth.authn.jwt.AuthRefreshTokenService;
import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.authn.session.Session;
import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.pojo.vo.Result;
import com.nimbusds.jwt.JWTClaimsSet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 前端认证令牌刷新/token/refresh
 *
 * @author Crystal.Sea
 *
 */
@Tag(name = "令牌刷新接口")
@Slf4j
@RestController
@RequestMapping(value = "/token")
public class TokenRefreshPoint {
/**
	 * 认证令牌服务
	 */
	@Autowired
	@Qualifier(value ="authTokenService")
	AuthTokenService authTokenService;
	/**
	 * 刷新令牌服务
	 */
	@Autowired
	@Qualifier(value ="refreshTokenService")
	AuthRefreshTokenService refreshTokenService;
	/**
	 * 会话管理
	 */
	@Autowired
	SessionManager sessionManager;

 	/**
 	 * 根据refreshToken刷新认证令牌
 	 * @param refreshToken
 	 * @return
 	 */
	@Operation(summary = "刷新认证令牌", description = "根据refresh_token刷新认证令牌",method="POST")
	@ApiResponse(
            responseCode = "200",
            description = "成功"
    )
	@PostMapping(value={"/refresh"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<AuthJwt> refresh(@RequestParam(name = "refresh_token", required = false) String refreshToken) {
 		log.debug("try to refresh token " );
 		log.trace("refresh token {}" , refreshToken);
 		try {
 			//判断refreshToken的有效性
	 		if (StringUtils.isNotBlank(refreshToken) && refreshTokenService.validateJwtToken(refreshToken)) {
	 			//解析refreshToken，转换会话id
	 			JWTClaimsSet claim = refreshTokenService.resolve(refreshToken);
	 			String sessionId = claim.getJWTID();
	 			//尝试刷新会话
	 			log.trace("Try to  refresh  sessionId [{}]", sessionId);
		 		Session session = sessionManager.refresh(sessionId);
		 		if(session != null) {//有会话
		 			//重新生成新令牌
		 			AuthJwt authJwt = authTokenService.genAuthJwt(session.getAuthentication());
		 			log.trace("Grant new token {}", authJwt);
		 			return Result.success(authJwt);
		 		}
				log.debug("Session is timeout , sessionId [{}]" , sessionId);
				throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "refresh_token缺失或已过期");
	 		}
 		}catch(Exception e) {
			log.error("Refresh Exception !", e);
		}
		throw new BusinessException(601,"令牌刷新失败");
 	}
}
