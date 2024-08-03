


package com.ldapauth.authn.jwt;

import java.text.ParseException;

import com.ldapauth.cache.CacheService;
import org.apache.commons.lang3.StringUtils;
import com.ldapauth.configuration.AuthJwkConfig;
import com.ldapauth.crypto.jwt.HMAC512Service;
import com.ldapauth.cache.MomentaryService;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.nimbusds.jose.JOSEException;

/**
 * 认证令牌服务
 *
 * @author Crystal.Sea
 *
 */
public class AuthTokenService  extends AuthJwtService{
	private static final  Logger _logger = LoggerFactory.getLogger(AuthTokenService.class);

	/**
	 * 前端JWT生成配置信息
	 */
	AuthJwkConfig authJwkConfig;
	/**
	 * congress服务
	 */
	CongressService congressService;
	/**
	 * 缓存服务
	 */
	MomentaryService momentaryService;

	CacheService cacheService;
	/**
	 * 认证刷新token服务
	 */
	AuthRefreshTokenService refreshTokenService;

	public AuthTokenService(
				AuthJwkConfig authJwkConfig,
				CacheService cacheService,
				CongressService congressService,
				MomentaryService momentaryService,
				AuthRefreshTokenService refreshTokenService) throws JOSEException {

		this.authJwkConfig = authJwkConfig;

		this.congressService = congressService;

		this.momentaryService = momentaryService;

		this.refreshTokenService = refreshTokenService;

		this.hmac512Service = new HMAC512Service(authJwkConfig.getSecret());

		this.cacheService = cacheService;

	}

	/**
	 * create AuthJwt use Authentication JWT/根据认证信息生成认证JWT
	 * @param authentication
	 * @return AuthJwt
	 */
	public AuthJwt genAuthJwt(Authentication authentication) {
		if(authentication != null) {
			String refreshToken = refreshTokenService.genRefreshToken(authentication);
			_logger.trace("generate JWT Token");
			String accessToken = genJwt(authentication);
			AuthJwt authJwt = new AuthJwt(
						accessToken,
						authentication,
						authJwkConfig.getExpires(),
						refreshToken);
			return authJwt;
		}
		return null;
	}

	public String genJwt(Authentication authentication) {
		return genJwt(
					authentication,
					authJwkConfig.getIssuer(),
					authJwkConfig.getExpires());
	}


	/**
	 * JWT with subject/subject生成JWT
	 * @param subject subject
	 * @return
	 */
	public String genJwt(String subject) {
		return genJwt(subject,authJwkConfig.getIssuer(),authJwkConfig.getExpires());
	}

	/**
	 * Random JWT/随机JWT
	 * @return
	 */
	public String genRandomJwt() {
		return genRandomJwt(authJwkConfig.getRefreshExpires());
	}

	/**
	 * 创建congress
	 * @param authentication
	 * @return congress
	 */
	public String createCongress(Authentication  authentication) {
		String congress = WebContext.genId();
		String refreshToken = refreshTokenService.genRefreshToken(authentication);
		congressService.store(
				congress,
				new AuthJwt(
						genJwt(authentication),
						authentication,
						authJwkConfig.getExpires(),
						refreshToken)
			);
		return congress;
	}

	/**
	 * 消费congress
	 * @param congress
	 * @return AuthJwt
	 */
	public AuthJwt consumeCongress(String congress) {
		AuthJwt authJwt = congressService.consume(congress);
		return authJwt;
	}

	/**
	 * 验证码校验
	 * @param state
	 * @param captcha
	 * @return boolean
	 */
	public boolean validateCaptcha(String state,String captcha) {
    	try {
			String jwtId = resolveJWTID(state);
			if(StringUtils.isNotBlank(jwtId) &&StringUtils.isNotBlank(captcha)) {
				String momentaryCaptcha = cacheService.getCacheObject(jwtId);
		        _logger.debug("captcha : {}, momentary Captcha : {}" ,captcha, momentaryCaptcha);
		        if (!StringUtils.isBlank(captcha) && captcha.equals(momentaryCaptcha)) {
					cacheService.deleteObject(jwtId);
		        	return true;
		        }
			}
		} catch (ParseException e) {
			 _logger.debug("Exception ",e);
		}
    	 return false;
    }


}
