

package com.ldapauth.authn.support.jwt;

import com.ldapauth.authn.LoginCredential;
import com.ldapauth.authn.jwt.AuthJwt;
import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.entity.Message;
import com.ldapauth.authn.provider.AbstractAuthenticationProvider;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.constants.ConstsLoginType;
import com.ldapauth.web.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.nimbusds.jwt.SignedJWT;

/**
 * JWT信任登录
 * <p>
 * /login/jwt 后台登录验证
 * </p>
 * <p>
 * /login/jwt/trust 使用相同密钥
 * </p>
 * @author shimi
 *
 */
@Controller
@RequestMapping(value = "/login")
public class HttpJwtEntryPoint {
	private static final Logger _logger = LoggerFactory.getLogger(HttpJwtEntryPoint.class);

	@Autowired
  	ApplicationConfig applicationConfig;

  	@Autowired
    AbstractAuthenticationProvider authenticationProvider ;

  	@Autowired
    AuthTokenService authTokenService;

    @Autowired
	JwtLoginService jwtLoginService;

	@RequestMapping(value={"/jwt"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> jwt(@RequestParam(value = WebConstants.JWT_TOKEN_PARAMETER, required = true) String jwt) {
		try {
			//for jwt Login
			 _logger.debug("jwt : " + jwt);

			 SignedJWT signedJWT = jwtLoginService.jwtTokenValidation(jwt);

			 if(signedJWT != null) {
				 String username =signedJWT.getJWTClaimsSet().getSubject();
				 LoginCredential loginCredential =new LoginCredential(username,"",ConstsLoginType.JWT);
				 Authentication  authentication = authenticationProvider.authenticate(loginCredential,true);
				 _logger.debug("JWT Logined in , username " + username);
				 AuthJwt authJwt = authTokenService.genAuthJwt(authentication);
		 		 return new Message<AuthJwt>(authJwt).buildResponse();
			 }
		}catch(Exception e) {
			_logger.error("Exception ",e);
		}

		 return new Message<AuthJwt>(Message.FAIL).buildResponse();
	}

	/**
	 * trust same HS512
	 * @param jwt
	 * @return
	 */
	@RequestMapping(value={"/jwt/trust"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> jwtTrust(@RequestParam(value = WebConstants.JWT_TOKEN_PARAMETER, required = true) String jwt) {
		try {
			//for jwt Login
			 _logger.debug("jwt : " + jwt);

			 if(authTokenService.validateJwtToken(jwt)) {
				 String username =authTokenService.resolve(jwt).getSubject();
				 LoginCredential loginCredential =new LoginCredential(username,"",ConstsLoginType.JWT);
				 Authentication  authentication = authenticationProvider.authenticate(loginCredential,true);
				 _logger.debug("JWT Logined in , username " + username);
				 AuthJwt authJwt = authTokenService.genAuthJwt(authentication);
		 		 return new Message<AuthJwt>(authJwt).buildResponse();
			 }
		}catch(Exception e) {
			_logger.error("Exception ",e);
		}

		 return new Message<AuthJwt>(Message.FAIL).buildResponse();
	}


	public void setApplicationConfig(ApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public void setAuthenticationProvider(AbstractAuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}

	public void setJwtLoginService(JwtLoginService jwtLoginService) {
		this.jwtLoginService = jwtLoginService;
	}

}
