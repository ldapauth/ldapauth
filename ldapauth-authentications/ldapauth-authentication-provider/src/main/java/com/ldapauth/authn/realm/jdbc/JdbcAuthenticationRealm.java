package com.ldapauth.authn.realm.jdbc;

import com.ldapauth.authn.realm.AbstractAuthenticationRealm;
import com.ldapauth.authn.realm.ldap.LdapAuthenticationRealm;
import com.ldapauth.constants.ConstsStatus;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.pojo.entity.PolicyLogin;
import com.ldapauth.persistence.repository.LoginRepository;
import com.ldapauth.persistence.repository.PasswordPolicyValidator;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.constants.ConstsLoginType;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.ip2region.IpRegionParser;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * JdbcAuthenticationRealm.数据认证域
 *
 * @author Crystal.Sea
 *
 */
public class JdbcAuthenticationRealm extends AbstractAuthenticationRealm {
    private static Logger _logger = LoggerFactory.getLogger(JdbcAuthenticationRealm.class);

    protected PasswordEncoder passwordEncoder;

    public JdbcAuthenticationRealm() {
        _logger.debug("init . ");
    }

    public JdbcAuthenticationRealm(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


	LdapAuthenticationRealm ldapAuthenticationRealm;

    public JdbcAuthenticationRealm(
    		PasswordEncoder passwordEncoder,
    		PasswordPolicyValidator passwordPolicyValidator,
    		LoginRepository loginRepository,
    		UserInfoService userInfoService,
    	    IpRegionParser ipRegionParser) {
    	this.passwordEncoder =passwordEncoder;
    	this.passwordPolicyValidator=passwordPolicyValidator;
    	this.loginRepository = loginRepository;
    	this.userInfoService = userInfoService;
        this.ipRegionParser = ipRegionParser;
    }

    public JdbcAuthenticationRealm(
    		PasswordEncoder passwordEncoder,
    		PasswordPolicyValidator passwordPolicyValidator,
    		LoginRepository loginRepository,
    		UserInfoService userInfoService,
    	    JdbcTemplate jdbcTemplate,
    	    IpRegionParser ipRegionParser,
			LdapAuthenticationRealm ldapAuthenticationRealm) {
		this.passwordEncoder = passwordEncoder;
		this.passwordPolicyValidator = passwordPolicyValidator;
		this.loginRepository = loginRepository;
		this.userInfoService = userInfoService;
		this.ipRegionParser = ipRegionParser;
		this.ldapAuthenticationRealm = ldapAuthenticationRealm;
    }

    /**
     * passwordMatches.
     */
    public boolean passwordMatches(UserInfo userInfo, String password) {
        boolean passwordMatches = false;
		//判断来源LDAP
       if (userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.OPEN_LDAP) &&
			   userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.ACTIVEDIRECTORY)) {
	        try {
				passwordMatches = ldapAuthenticationRealm.passwordMatches(userInfo, password);
				if (passwordMatches) {
					_logger.info("ldapuser:{} Authentication success",userInfo.getUsername());
				}
	        }catch(Exception e) {
	        	_logger.debug("passwordvalid Exception : {}" , e);
	        }
        }
	    //本地认证
	    else if(userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM) ||
			   userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.FEISHU) ||
			   userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.WORKWEIXIN) ||
			   userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.DINGDING)
	   ) {
		   passwordMatches = passwordEncoder.matches(password,userInfo.getPassword());
	    }
        _logger.debug("passwordvalid : {}" , passwordMatches);
        if (!passwordMatches) {
            passwordPolicyValidator.plusBadPasswordCount(userInfo);
            insertLoginHistory(userInfo, ConstsLoginType.LOCAL, "系统登录", "xe00000004", WebConstants.LOGIN_RESULT.PASSWORD_ERROE);
			PolicyLogin passwordPolicy = passwordPolicyValidator.getPasswordPolicyRepository().getPolicyLogin();
			//首次登录不弹出失败多少次，第二次开始弹出
			if (userInfo.getBadPasswordCount() >= 2 && userInfo.getBadPasswordCount() < passwordPolicy.getPasswordAttempts()) {
				throw new BadCredentialsException(
						WebContext.getI18nValue("login.error.password.attempts",
								new Object[]{
										userInfo.getBadPasswordCount(),
										passwordPolicy.getPasswordAttempts(),
										passwordPolicy.getLoginLockInterval()}));
			} else if (userInfo.getBadPasswordCount() >= passwordPolicy.getPasswordAttempts()) {
				throw new BadCredentialsException(
						WebContext.getI18nValue("login.error.attempts",
								new Object[]{passwordPolicy.getPasswordAttempts(),passwordPolicy.getLoginLockInterval()})
				);
			}
			else {
				throw new BadCredentialsException(WebContext.getI18nValue("login.error.password"));
			}
        }
        return passwordMatches;
    }

}
