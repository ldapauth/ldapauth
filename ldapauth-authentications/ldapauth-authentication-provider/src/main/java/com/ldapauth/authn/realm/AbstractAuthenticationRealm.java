package com.ldapauth.authn.realm;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ldapauth.ip2region.Region;
import com.ldapauth.persistence.repository.LoginRepository;
import com.ldapauth.persistence.repository.PasswordPolicyValidator;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.entity.LoginLog;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.ip2region.IpRegionParser;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;

/**
 * AbstractAuthenticationRealm.认证域抽象类
 *
 * @author Crystal.Sea
 *
 */
public abstract class AbstractAuthenticationRealm {
    private static Logger _logger = LoggerFactory.getLogger(AbstractAuthenticationRealm.class);

    protected JdbcTemplate jdbcTemplate;

    protected PasswordPolicyValidator passwordPolicyValidator;

    protected LoginRepository loginRepository;


    protected UserInfoService userInfoService;

    protected IpRegionParser ipRegionParser;
    /**
     * 定义线程池，写入日志
     */
    ExecutorService executorService = Executors.newFixedThreadPool(200);

    /**
     *
     */
    public AbstractAuthenticationRealm() {

    }

    public AbstractAuthenticationRealm(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PasswordPolicyValidator getPasswordPolicyValidator() {
        return passwordPolicyValidator;
    }

    public LoginRepository getLoginRepository() {
        return loginRepository;
    }

    public UserInfo loadUserInfo(String username, String password) {
        return loginRepository.find(username, password);
    }

    public abstract boolean passwordMatches(UserInfo userInfo, String password);


    /**
     * grant Authority by userinfo
     *
     * @param userInfo
     * @return ArrayList<GrantedAuthority>
     */
    public ArrayList<GrantedAuthority> grantAuthority(UserInfo userInfo) {
        return loginRepository.grantAuthority(userInfo);
    }

    /**
     * grant Authority by grantedAuthoritys
     *
     * @param grantedAuthoritys
     * @return ArrayList<GrantedAuthority Apps>
     */
    public ArrayList<GrantedAuthority> queryAuthorizedApps(UserInfo userInfo , ArrayList<GrantedAuthority> grantedAuthoritys) {
        return loginRepository.queryAuthorizedApps(userInfo , grantedAuthoritys);
    }


    /**
     * login log write to log db
     *
     * @param type
     * @param code
     * @param message
     */
    public boolean insertLoginHistory(UserInfo userInfo, String type, String provider, String code, String message) {
        LoginLog historyLogin = new LoginLog();

        String requestIpAddress = WebContext.getRequestIpAddress();
        //test
        //requestIpAddress = "101.227.131.220";
        historyLogin.setUserId(userInfo.getId());
        historyLogin.setDisplayName(userInfo.getDisplayName());
        historyLogin.setLoginType(type);
        historyLogin.setMessage(message);
        if(StringUtils.isNotEmpty(provider)) {
            historyLogin.setProvider(provider);
        } else {
            historyLogin.setProvider("系统登录");
        }

        //browser info
        Browser browser = resolveBrowser();
        historyLogin.setBrowser(browser.getName());
        historyLogin.setOperateSystem(browser.getPlatform());


        //ip address to region
        historyLogin.setIpAddr(requestIpAddress);
        Region region = ipRegionParser.region(requestIpAddress);
        historyLogin.setCountry(region.getCountry());
        historyLogin.setProvince(region.getProvince());
        historyLogin.setCity(region.getCity());
        if (message.equalsIgnoreCase(WebConstants.LOGIN_RESULT.SUCCESS)) {
            historyLogin.setStatus(0);
        } else{
            historyLogin.setStatus(1);
        }
        historyLogin.setMessage(message);
        historyLogin.setCreateTime(new Date());

        //异步线程处理
        executorService.execute(() -> {
            //insert
            loginRepository.history(historyLogin);

            userInfo.setLastLoginIp(requestIpAddress);
            //update user last info
            loginRepository.updateLastLogin(userInfo);
        });
        return true;
    }

    public Browser  resolveBrowser() {
        Browser browser =new Browser();
        String userAgent = WebContext.getRequest().getHeader("User-Agent");
        String[] arrayUserAgent = null;
        if (userAgent.indexOf("MSIE") > 0) {
            arrayUserAgent = userAgent.split(";");
            browser.setName(arrayUserAgent[1].trim());
            browser.setPlatform(arrayUserAgent[2].trim());
        } else if (userAgent.indexOf("Trident") > 0) {
            arrayUserAgent = userAgent.split(";");
            browser.setName( "MSIE/" + arrayUserAgent[3].split("\\)")[0]);

            browser.setPlatform( arrayUserAgent[0].split("\\(")[1]);
        } else if (userAgent.indexOf("Chrome") > 0) {
            arrayUserAgent = userAgent.split(" ");
            // browser=arrayUserAgent[8].trim();
            for (int i = 0; i < arrayUserAgent.length; i++) {
                if (arrayUserAgent[i].contains("Chrome")) {
                    browser.setName( arrayUserAgent[i].trim());
                    browser.setName( browser.getName().substring(0, browser.getName().indexOf('.')));
                }
            }
            browser.setPlatform( (arrayUserAgent[1].substring(1) + " " + arrayUserAgent[2] + " "
                    + arrayUserAgent[3].substring(0, arrayUserAgent[3].length() - 1)).trim());
        } else if (userAgent.indexOf("Firefox") > 0) {
            arrayUserAgent = userAgent.split(" ");
            for (int i = 0; i < arrayUserAgent.length; i++) {
                if (arrayUserAgent[i].contains("Firefox")) {
                    browser.setName( arrayUserAgent[i].trim());
                    browser.setName(browser.getName().substring(0, browser.getName().indexOf('.')));
                }
            }
            browser.setPlatform( (arrayUserAgent[1].substring(1) + " " + arrayUserAgent[2] + " "
                    + arrayUserAgent[3].substring(0, arrayUserAgent[3].length() - 1)).trim());

        }

        return browser;
    }


    public class Browser{

        private  String platform;

        private  String name;

        public String getPlatform() {
            return platform;
        }
        public void setPlatform(String platform) {
            this.platform = platform;
        }
        public String getName() {
            return name;
        }
        public void setName(String browser) {
            this.name = browser;
        }


    }

}
