package com.ldapauth.authn.provider.impl;

import java.text.ParseException;

import com.ldapauth.authn.LoginCredential;
import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.authn.provider.AbstractAuthenticationProvider;
import com.ldapauth.authn.realm.AbstractAuthenticationRealm;
import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.constants.ConstsLoginType;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Normal Authentication provider.正常用户名密码认证提供者
 *
 * @author Crystal.Sea
 *
 */
public class NormalAuthenticationProvider extends AbstractAuthenticationProvider {
    private static final Logger _logger =
            LoggerFactory.getLogger(NormalAuthenticationProvider.class);

    public String getProviderName() {
        return "normal" + PROVIDER_SUFFIX;
    }


    public NormalAuthenticationProvider() {
		super();
	}

    public NormalAuthenticationProvider(
    		AbstractAuthenticationRealm authenticationRealm,
    		ApplicationConfig applicationConfig,
    	    SessionManager sessionManager,
    	    AuthTokenService authTokenService) {
		this.authenticationRealm = authenticationRealm;
		this.applicationConfig = applicationConfig;
		this.sessionManager = sessionManager;
		this.authTokenService = authTokenService;
	}

    @Override
	public Authentication doAuthenticate(LoginCredential loginCredential) {
		UsernamePasswordAuthenticationToken authenticationToken = null;
		_logger.debug("Trying to authenticate user '{}' via {}",
                loginCredential.getPrincipal(), getProviderName());
        try {

	        _logger.debug("authentication " + loginCredential);

			captchaValid(loginCredential.getState(),loginCredential.getCaptcha());

	        emptyPasswordValid(loginCredential.getPassword());

	        emptyUsernameValid(loginCredential.getUsername());
	        //查询用户
	        UserInfo userInfo =  loadUserInfo(loginCredential.getUsername(),loginCredential.getPassword());
			//验证状态
	        statusValid(loginCredential , userInfo);

	        //Validate PasswordPolicy
	        authenticationRealm.getPasswordPolicyValidator().passwordPolicyValid(userInfo);

	        //Match password 验证密码
	        authenticationRealm.passwordMatches(userInfo, loginCredential.getPassword());

	        //apply PasswordSetType and resetBadPasswordCount
	        authenticationRealm.getPasswordPolicyValidator().applyPasswordPolicy(userInfo);

	        authenticationToken = createOnlineTicket(loginCredential,userInfo);
	        // user authenticated
	        _logger.debug("'{}' authenticated successfully by {}.",
	        		loginCredential.getPrincipal(), getProviderName());

	        authenticationRealm.insertLoginHistory(userInfo,
							        				ConstsLoginType.LOCAL,
									                "系统登录",
									                "xe00000004",
									                WebConstants.LOGIN_RESULT.SUCCESS);
        } catch (AuthenticationException e) {
            _logger.error("Failed to authenticate user {} via {}: {}",
                    new Object[] {  loginCredential.getPrincipal(),
                                    getProviderName(),
                                    e.getMessage() });
            WebContext.setAttribute(
                    WebConstants.LOGIN_ERROR_SESSION_MESSAGE, e.getMessage());
        } catch (Exception e) {
            _logger.error("Login error Unexpected exception in {} authentication:\n{}" ,
                            getProviderName(), e.getMessage());
        }

        return  authenticationToken;
    }

    /**
     * captcha validate .图片验证码校验
     *
     * @param captcha String
     * @throws ParseException
     */
    protected void captchaValid(String state ,String captcha)  {
		if (applicationConfig.getLoginConfig().isCaptcha()) {
			// for basic
			if (!authTokenService.validateCaptcha(state, captcha)) {
				throw new BadCredentialsException(WebContext.getI18nValue("login.error.captcha"));
			}
		}
    }
}
