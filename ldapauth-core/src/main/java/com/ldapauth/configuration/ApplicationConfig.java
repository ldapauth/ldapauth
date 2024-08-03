package com.ldapauth.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 全局应用程序配置 包含 1、数据源配置 dataSoruceConfig 2、字符集转换配置 characterEncodingConfig
 * 3、webseal认证集成配置 webSealConfig 4、系统的配置 sysConfig 5、所有用户可访问地址配置 allAccessUrl
 *
 * 其中1、2、3项在applicationContext.xml中配置，配置文件applicationConfig.properties
 * 4项根据dynamic的属性判断是否动态从sysConfigService动态读取
 *
 * @author Crystal.Sea
 *
 */
@Component
@Configuration
@RefreshScope
public class ApplicationConfig {


    /**
     * 缓存策略 #InMemory 0 , Redis 2
     */
    @Value("${ldapauth.server.persistence:0}")
    Integer persistence;

	@Value("${ldapauth.server.basedomain}")
    String baseDomainName;

    @Value("${ldapauth.server.domain}")
    String domainName;

    @Value("${ldapauth.server.name}")
    String serverName;

    @Value("${ldapauth.server.uri}")
    String serverPrefix;

    @Value("${ldapauth.server.default.uri}")
    String defaultUri;

    @Value("${ldapauth.server.mgt.uri}")
    String mgtUri;

    @Value("${ldapauth.server.authz.uri}")
    private String authzUri;

    @Value("${ldapauth.server.frontend.uri:http://sso.ldapauth.com:4200}")
    private String frontendUri;

    @Value("${server.port:8080}")
    private int port;

    @Value("${server.servlet.session.timeout:1800}")
    private int sessionTimeout;

    @Value("${ldapauth.server.provision:none}")
    private String provision;

    @Value("${ldapauth.notices.visible:false}")
    private boolean noticesVisible;

    public static String  databaseProduct = "MySQL";

    @Autowired
    CharacterEncodingConfig characterEncodingConfig;

    @Autowired
    LoginConfig loginConfig;


    public Integer getPersistence() {
        return persistence;
    }

    public void setPersistence(Integer persistence) {
        this.persistence = persistence;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ApplicationConfig() {
        super();
    }

    /**
     * @return the characterEncodingConfig
     */
    public CharacterEncodingConfig getCharacterEncodingConfig() {
        return characterEncodingConfig;
    }

    /**
     * @param characterEncodingConfig the characterEncodingConfig to set
     */
    public void setCharacterEncodingConfig(CharacterEncodingConfig characterEncodingConfig) {
        this.characterEncodingConfig = characterEncodingConfig;
    }

    public LoginConfig getLoginConfig() {
        return loginConfig;
    }

    public void setLoginConfig(LoginConfig loginConfig) {
        this.loginConfig = loginConfig;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerPrefix() {
        return serverPrefix;
    }

    public void setServerPrefix(String serverPrefix) {
        this.serverPrefix = serverPrefix;
    }

    public String getFrontendUri() {
		return frontendUri;
	}

	public void setFrontendUri(String frontendUri) {
		this.frontendUri = frontendUri;
	}

	/**
     * @return the domainName
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * @param domainName the domainName to set
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;

    }

    public String getBaseDomainName() {
        return baseDomainName;
    }

    public void setBaseDomainName(String baseDomainName) {
        this.baseDomainName = baseDomainName;
    }




    public String getDefaultUri() {
        return defaultUri;
    }

    public void setDefaultUri(String defaultUri) {
        this.defaultUri = defaultUri;
    }


    public String getProvision() {
		return provision;
	}

	public void setProvision(String provision) {
		this.provision = provision;
	}

	public boolean isProvisionSupport() {
        if (true) {
            return true;
        }
    	if(StringUtils.isBlank(provision)||provision.equalsIgnoreCase("none")) {
    		return false;
    	}
		return true;
	}

	public String getMgtUri() {
		return mgtUri;
	}

	public void setMgtUri(String mgtUri) {
		this.mgtUri = mgtUri;
	}

	public String getAuthzUri() {
		return authzUri;
	}

	public void setAuthzUri(String authzUri) {
		this.authzUri = authzUri;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public boolean isNoticesVisible() {
		return noticesVisible;
	}

	public void setNoticesVisible(boolean noticesVisible) {
		this.noticesVisible = noticesVisible;
	}


}
