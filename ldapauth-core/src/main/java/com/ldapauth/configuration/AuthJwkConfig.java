


package com.ldapauth.configuration;

import com.ldapauth.persistence.service.PolicyLoginService;
import com.ldapauth.pojo.entity.PolicyLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 令牌JWT参数基本配置，
 *
 * @author Crystal.Sea
 *
 */
@Component
@Configuration
public class AuthJwkConfig {
	/**
	 * 令牌有效时间
	 */
	@Value("${ldapauth.auth.jwt.expires:86400}")
	int 	expires;
	/**
	 * 令牌密钥
	 */
	@Value("${ldapauth.auth.jwt.secret}")
	String 	secret;
	/**
	 * 刷新令牌有效时间，refreshExpires = expires / 2
	 */
	@Value("${ldapauth.session.timeout}")
	int 	refreshExpires;
	/**
	 * 刷新令牌密钥
	 */
	@Value("${ldapauth.auth.jwt.refresh.secret}")
	String 	refreshSecret;
	/**
	 * 令牌签发者
	 */
	@Value("${ldapauth.auth.jwt.issuer:https://sso.ldapauth.com/}")
	String 	issuer;

	@Autowired
	PolicyLoginService policyLoginService;

	public AuthJwkConfig() {
		super();
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}


	public int getExpires() {
		PolicyLogin policy = policyLoginService.getCache();
		if (Objects.nonNull(policy)) {
			return policy.getAccessTokenValidity();
		}
		return expires;
	}

	public void setExpires(int expires) {
		this.expires = expires;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public int getRefreshExpires() {
		PolicyLogin policy = policyLoginService.getCache();
		if (Objects.nonNull(policy)) {
			return policy.getRefreshTokenValidity();
		}
		return refreshExpires;
	}

	public void setRefreshExpires(int refreshExpires) {

		this.refreshExpires = refreshExpires;
	}

	public String getRefreshSecret() {
		return refreshSecret;
	}

	public void setRefreshSecret(String refreshSecret) {
		this.refreshSecret = refreshSecret;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthJwkConfig [issuer=");
		builder.append(issuer);
		builder.append(", expires=");
		builder.append(expires);
		builder.append(", secret=");
		builder.append(secret);
		builder.append("]");
		return builder.toString();
	}

}
