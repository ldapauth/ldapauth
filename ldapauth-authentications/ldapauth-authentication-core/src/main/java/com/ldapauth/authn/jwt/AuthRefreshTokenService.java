


package com.ldapauth.authn.jwt;

import com.ldapauth.configuration.AuthJwkConfig;
import com.ldapauth.crypto.jwt.HMAC512Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.nimbusds.jose.JOSEException;

/**
 * 刷新令牌服务
 *
 * @author Crystal.Sea
 *
 */
public class AuthRefreshTokenService extends AuthJwtService{
	private static final  Logger _logger = LoggerFactory.getLogger(AuthRefreshTokenService.class);

	/**
	 * 前端JWT生成配置信息
	 */
	AuthJwkConfig authJwkConfig;

	public AuthRefreshTokenService(AuthJwkConfig authJwkConfig) throws JOSEException {
		this.authJwkConfig = authJwkConfig;
		/**
		 * HMAC512签名服务
		 */
		this.hmac512Service = new HMAC512Service(authJwkConfig.getRefreshSecret());
	}

	/**
	 * JWT Refresh Token with Authentication/根据认证信息生成JWT刷新令牌
	 * @param authentication
	 * @return String
	 */
	public String genRefreshToken(Authentication authentication) {
		_logger.trace("generate Refresh JWT Token");
		return genJwt(
				 authentication,
				 authJwkConfig.getIssuer(),
				 authJwkConfig.getRefreshExpires());
	}
}
