


package com.ldapauth.authn.jwt;

import java.text.ParseException;
import java.util.Date;

import com.ldapauth.authn.SignPrincipal;
import org.joda.time.DateTime;
import com.ldapauth.crypto.jwt.HMAC512Service;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.util.StringUtils;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * 认证JWT生成服务
 *
 * @author Crystal.Sea
 *
 */
public class AuthJwtService {
	private static final  Logger _logger = LoggerFactory.getLogger(AuthJwtService.class);

	/**
	 * HMAC512签名服务
	 */
	HMAC512Service hmac512Service;

	/**
	 * JWT with Authentication/根据认证信息生成JWT
	 * @param authentication
	 * @return
	 */
	public String genJwt(Authentication authentication,String issuer,int expires) {
		SignPrincipal principal = ((SignPrincipal)authentication.getPrincipal());
		DateTime currentDateTime = DateTime.now();
		String subject = principal.getUsername();
		Date expirationTime = currentDateTime.plusSeconds(expires).toDate();
		_logger.trace("jwt subject : {} , expiration Time : {}" , subject,expirationTime);
		 JWTClaimsSet jwtClaims =new  JWTClaimsSet.Builder()
				.issuer(issuer)
				.subject(subject)
				.jwtID(principal.getSession().getId())
				.issueTime(currentDateTime.toDate())
				.expirationTime(expirationTime)
				.claim("kid", HMAC512Service.MXK_AUTH_JWK)
				.build();

		return signedJWT(jwtClaims);
	}

	/**
	 * JWT with subject/根据subject , issuer , expires生成JWT
	 * @param subject subject
	 * @return
	 */
	public String genJwt(String subject,String issuer,int expires) {
		DateTime currentDateTime = DateTime.now();
		Date expirationTime = currentDateTime.plusSeconds(expires).toDate();
		_logger.trace("jwt subject : {} , expiration Time : {}" , subject,expirationTime);

		 JWTClaimsSet jwtClaims =new  JWTClaimsSet.Builder()
				.issuer(issuer)
				.subject(subject)
				.jwtID(WebContext.genId())
				.issueTime(currentDateTime.toDate())
				.expirationTime(expirationTime)
				.build();

		return signedJWT(jwtClaims);
	}

	/**
	 * Random JWT/随机生成JWT
	 * @return
	 */
	public String genRandomJwt(int expires) {
		Date expirationTime = DateTime.now().plusSeconds(expires).toDate();
		_logger.trace("expiration Time : {}" , expirationTime);

		 JWTClaimsSet jwtClaims =new  JWTClaimsSet.Builder()
				.jwtID(WebContext.genId())
				.expirationTime(expirationTime)
				.build();

		return signedJWT(jwtClaims);
	}

	/**
	 * JWT HS512签名claims数据
	 * @param jwtClaims
	 * @return jwt string
	 */
	public String signedJWT(JWTClaimsSet jwtClaims) {
		_logger.trace("jwt Claims : {}" , jwtClaims);
		SignedJWT  jwtToken = new SignedJWT(
				new JWSHeader(JWSAlgorithm.HS512),
				jwtClaims);
		return hmac512Service.sign(jwtToken.getPayload());
	}

	/**
	 *
	 * Verify with HMAC512 and check ExpirationTime
	 * <p>使用HMAC512验证JWT并检查是否过期</p>
	 *
	 * @param authToken
	 * @return true or false
	 */
	public boolean validateJwtToken(String authToken) {
		try {
			if(StringUtils.isNotBlank(authToken)) {
				JWTClaimsSet claims = resolve(authToken);
				boolean isExpiration = claims.getExpirationTime().after(DateTime.now().toDate());
				boolean isVerify = hmac512Service.verify(authToken);
				_logger.debug("JWT Validate {} " , isVerify && isExpiration);
				if(!(isVerify && isExpiration)) {
					_logger.debug("HMAC Verify {} , now {} , ExpirationTime {} , is not Expiration : {}" ,
							isVerify,DateTime.now().toDate(),claims.getExpirationTime(),isExpiration);
				}
				return isVerify && isExpiration;
			}
		} catch (ParseException e) {
			_logger.error("authToken {}",authToken);
			_logger.error("ParseException ",e);
		}
		return false;
	}

	/**
	 * 解析JWT的Claims字段
	 * @param authToken
	 * @return
	 * @throws ParseException
	 */
	public  JWTClaimsSet resolve(String authToken) throws ParseException {
		SignedJWT signedJWT = SignedJWT.parse(authToken);
		_logger.trace("jwt Claims : {}" , signedJWT.getJWTClaimsSet());
		return signedJWT.getJWTClaimsSet();
	}

	/**
	 * 解析JWTID
	 * @param authToken
	 * @return
	 * @throws ParseException
	 */
	public String resolveJWTID(String authToken) throws ParseException {
		JWTClaimsSet claims = resolve(authToken);
		return claims.getJWTID();
	}
}
