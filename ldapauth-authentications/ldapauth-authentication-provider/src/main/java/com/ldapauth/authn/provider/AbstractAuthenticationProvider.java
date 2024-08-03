package com.ldapauth.authn.provider;

import java.util.ArrayList;

import com.ldapauth.authn.LoginCredential;
import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.authn.realm.AbstractAuthenticationRealm;
import com.ldapauth.authn.session.Session;
import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.password.onetimepwd.AbstractOtpAuthn;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.constants.ConstsLoginType;
import com.ldapauth.constants.ConstsRoles;
import com.ldapauth.constants.ConstsStatus;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
/**
 * login Authentication abstract class.登录认证提供者抽象类
 *
 * @author Crystal.Sea
 *
 */
public abstract class AbstractAuthenticationProvider {
    private static final Logger _logger =
            LoggerFactory.getLogger(AbstractAuthenticationProvider.class);

    public static String PROVIDER_SUFFIX = "AuthenticationProvider";

    /**
     * 认证类型
     *
     */
    public class AuthType{
    	//用户名和密码登录
    	public final static String NORMAL 	= "normal";
    	//双因素认证
    	public final static String TFA 		= "tfa";
    	//手机号码登录
    	public final static String MOBILE 	= "mobile";
    	//HMAC512签名登录
    	public final static String HMACSIGN = "hmacSign";
    	//信任JWT登录
    	public final static String TRUSTED 	= "trusted";
    	//手机端APP登录
    	public final static String APP 		= "app";
    }

    protected ApplicationConfig applicationConfig;
    //认证域
    protected AbstractAuthenticationRealm authenticationRealm;
    //双因素OTP生成和验证服务
    protected AbstractOtpAuthn tfaOtpAuthn;

    //会话管理服务
    protected SessionManager sessionManager;
    //认证令牌服务
    protected AuthTokenService authTokenService;

    protected boolean supported = true;

    public static  ArrayList<GrantedAuthority> grantedAdministratorsAuthoritys = new ArrayList<>();

    /**
     * 默认角色
     */
    static {
    	//管理员角色
        grantedAdministratorsAuthoritys.add(ConstsRoles.ROLE_ADMINISTRATORS);
        //租户管理员角色
        grantedAdministratorsAuthoritys.add(ConstsRoles.ROLE_MANAGERS);
    }

    public abstract String getProviderName();

    public abstract Authentication doAuthenticate(LoginCredential authentication);

    @SuppressWarnings("rawtypes")
    public boolean supports(Class authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public Authentication authenticate(LoginCredential authentication){
    	return null;
    }

    public Authentication authenticate(LoginCredential authentication,boolean trusted) {
    	return null;
    }

    /**
     * createOnlineSession 认证成功后签发token及会话
     * @param credential
     * @param userInfo
     * @return
     */
    public UsernamePasswordAuthenticationToken createOnlineTicket(LoginCredential credential,UserInfo userInfo) {
        //create session/创建新用户会话
        Session session = new Session();
        session.setStyle(credential.getStyle());
        //set session with principal。设置认证当事人
        SignPrincipal principal = new SignPrincipal(userInfo,session);
        //读取用户授权角色
        ArrayList<GrantedAuthority> grantedAuthoritys = authenticationRealm.grantAuthority(userInfo);
        principal.setAuthenticated(true);
        //判断管理员角色
        for(GrantedAuthority administratorsAuthority : grantedAdministratorsAuthoritys) {
            if(grantedAuthoritys.contains(administratorsAuthority)) {
            	principal.setRoleAdministrators(true);
                _logger.trace("ROLE ADMINISTRATORS Authentication .");
            }
        }
        _logger.debug("Granted Authority {}" , grantedAuthoritys);
        //读取授权访问应用
        principal.setGrantedAuthorityApps(authenticationRealm.queryAuthorizedApps(userInfo,grantedAuthoritys));
        //创建认证token
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                		principal,
                        "PASSWORD",
                        grantedAuthoritys
                );

        authenticationToken.setDetails(
                new WebAuthenticationDetails(WebContext.getRequest()));

        /*
         *  put Authentication to current session context，设置会话的认证token
         */
        session.setAuthentication(authenticationToken);

        //create session。会话管理服务管理当前新会话。
        this.sessionManager.create(session.getId(), session);

        //set Authentication to http session，设置当前认证token
        AuthorizationUtils.setAuthentication(authenticationToken);

        return authenticationToken;
    }

    /**
     * login user by username ， userinfo is null,query user from system.
     *
     * @param username String
     * @param password String
     * @return
     */
    public UserInfo loadUserInfo(String username, String password) {
        UserInfo userInfo = authenticationRealm.loadUserInfo(username, password);
        if (userInfo != null) {
            if (userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
                _logger.debug("SYSTEM User Login. ");
            } else {
                _logger.debug("User Login. ");
            }

        }
        return userInfo;
    }

    /**
     * check input password empty.
     *
     * @param password String
     * @return
     */
    protected boolean emptyPasswordValid(String password) {
        if (null == password || "".equals(password)) {
            throw new BadCredentialsException(WebContext.getI18nValue("login.error.password.null"));
        }
        return true;
    }

    /**
     * check input username or password empty.
     *
     * @param email String
     * @return
     */
    protected boolean emptyEmailValid(String email) {
        if (null == email || "".equals(email)) {
            throw new BadCredentialsException("login.error.email.null");
        }
        return true;
    }

    /**
     * check input username empty.
     *
     * @param username String
     * @return
     */
    protected boolean emptyUsernameValid(String username) {
        if (null == username || "".equals(username)) {
            throw new BadCredentialsException(WebContext.getI18nValue("login.error.username.null"));
        }
        return true;
    }

    protected boolean statusValid(LoginCredential loginCredential , UserInfo userInfo) {
        if (null == userInfo) {
            String i18nMessage = WebContext.getI18nValue("login.error.username");
            _logger.debug("login user  " + loginCredential.getUsername() + " not in this System ." + i18nMessage);
            UserInfo loginUser = new UserInfo();
            loginUser.setUsername(loginCredential.getUsername());
            loginUser.setDisplayName("not exist");
            loginUser.setLoginCount(0);
            authenticationRealm.insertLoginHistory(
            			loginUser,
            			ConstsLoginType.LOCAL,
            			"系统登录",
            			i18nMessage,
            			WebConstants.LOGIN_RESULT.USER_NOT_EXIST);
            throw new BadCredentialsException(i18nMessage);
        }else {
        	if (userInfo.getIsLocked() == ConstsStatus.LOCK) {
        		authenticationRealm.insertLoginHistory(
        				userInfo,
                        loginCredential.getAuthType(),
                        loginCredential.getProvider(),
                        loginCredential.getCode(),
                        WebConstants.LOGIN_RESULT.USER_LOCKED
                    );
        	} else if(userInfo.getStatus() != ConstsStatus.DATA_ACTIVE) {
        		authenticationRealm.insertLoginHistory(
        				userInfo,
                        loginCredential.getAuthType(),
                        loginCredential.getProvider(),
                        loginCredential.getCode(),
                        WebConstants.LOGIN_RESULT.USER_INACTIVE
                    );
        	}
        }
        return true;
    }

	public boolean isSupported() {
		return supported;
	}

	public void setSupported(boolean supported) {
		this.supported = supported;
	}

}
