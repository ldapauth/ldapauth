package com.ldapauth.authz.singlelogout;

import java.util.Map;

import com.ldapauth.pojo.entity.apps.Apps;
import com.ldapauth.web.HttpRequestAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

public abstract class SingleLogout {
    private static final Logger _logger = LoggerFactory.getLogger(SingleLogout.class);

    public abstract void sendRequest(Authentication authentication, Apps logoutApp) ;

    public void postMessage(String url,Map<String, Object> paramMap) {
    	_logger.debug("post logout message to url {} " , url);
    	(new HttpRequestAdapter()).post(url , paramMap);
    }
}
