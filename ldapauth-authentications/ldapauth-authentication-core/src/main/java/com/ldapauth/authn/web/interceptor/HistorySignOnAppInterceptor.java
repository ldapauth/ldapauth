package com.ldapauth.authn.web.interceptor;

import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.AppLoginLogService;
import com.ldapauth.pojo.entity.apps.ClientAppsLoginLog;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.entity.apps.ClientApps;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class HistorySignOnAppInterceptor  implements AsyncHandlerInterceptor  {
    private static final Logger _logger = LoggerFactory.getLogger(HistorySignOnAppInterceptor.class);

    @Autowired
    AppLoginLogService appLoginLogService;

    public void postHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,ModelAndView modelAndView)  {
        _logger.debug("postHandle");

        final ClientApps app = (ClientApps) WebContext.getAttribute(WebConstants.AUTHORIZE_SIGN_ON_APP);
        SignPrincipal principal = AuthorizationUtils.getPrincipal();
        if(principal != null && app !=null) {
        	 final UserInfo userInfo = principal.getUserInfo();
        	 String sessionId = principal.getSession().getId();
        	 _logger.trace("sessionId : " + sessionId + " ,appId : " + app.getId());
             ClientAppsLoginLog historyLoginApps = new ClientAppsLoginLog();
             historyLoginApps.setAppId(app.getId());
             historyLoginApps.setAppName(app.getAppName());
             historyLoginApps.setUserId(userInfo.getId());
             historyLoginApps.setDisplayName(userInfo.getDisplayName());
             historyLoginApps.setCreateTime(new Date());
             appLoginLogService.save(historyLoginApps);
             WebContext.removeAttribute(WebConstants.CURRENT_SINGLESIGNON_URI);
             WebContext.removeAttribute(WebConstants.SINGLE_SIGN_ON_APP_ID);
        }

    }
}
