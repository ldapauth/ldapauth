package com.ldapauth.authn.web.interceptor;

import cn.hutool.core.util.URLUtil;
import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.authn.session.SessionManager;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.persistence.service.ClientAppsService;
import com.ldapauth.persistence.service.PermissionAuthenticationService;
import com.ldapauth.pojo.entity.apps.ClientApps;
import com.ldapauth.pojo.entity.apps.details.ClientAppsCASDetails;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 单点登录拦截器
 *
 * @author Crystal.Sea
 *
 */
@Component
public class SingleSignOnInterceptor  implements AsyncHandlerInterceptor {
    private static final Logger _logger = LoggerFactory.getLogger(SingleSignOnInterceptor.class);

    @Autowired
	ApplicationConfig applicationConfig;

    @Autowired
	SessionManager sessionManager;

    @Autowired
	AuthTokenService authTokenService ;

    @Autowired
	ClientAppsService appsService;

	/**
	 * 鉴权
	 */
	@Autowired
	PermissionAuthenticationService permissionAuthenticationService;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
    	_logger.trace("Single Sign On Interceptor");
       //获取认证信息
    	AuthorizationUtils.authenticateWithCookie(request,authTokenService,sessionManager);

        if(AuthorizationUtils.isNotAuthenticated()) {
        	//未登录，跳转到登录页面，带上当前应用请求参数base编码后
        	String loginUrl = applicationConfig.getFrontendUri() + "/login?redirect=%s";
        	String redirectUri = URLUtil.encodeAll(UrlUtils.buildFullRequestUrl(request));
        	_logger.debug(" redirect_uri {} , port {}",redirectUri ,request.getServerPort());
        	_logger.debug("No Authentication ... Redirect to /passport/login , redirect_uri {}",redirectUri);
        	response.sendRedirect(String.format(loginUrl,redirectUri));
        	return false;
        }

        //判断应用访问权限
        if(AuthorizationUtils.isAuthenticated()){
	        _logger.debug("preHandle {}",request.getRequestURI());
	        ClientApps app = (ClientApps) WebContext.getAttribute(WebConstants.AUTHORIZE_SIGN_ON_APP);
	        if(app == null) {
	        	String requestURI = request.getRequestURI();
	        	if(requestURI.contains("/authz/cas/login")) {//for CAS service
					ClientAppsCASDetails casDetails = appsService.getAppDetails(request.getParameter("serivce"), true);
					if (Objects.nonNull(casDetails)) {
						app = appsService.getById(casDetails.getAppId());
					}
	        	}else if(requestURI.contains("/authz/jwt/")
	        			||requestURI.contains("/authz/api/")
	        			||requestURI.contains("/authz/formbased/")
	        			||requestURI.contains("/authz/tokenbased/")
	        			||requestURI.contains("/authz/saml20/consumer/")
	        			||requestURI.contains("/authz/saml20/idpinit/")
	        			||requestURI.contains("/authz/cas/")
	        	) {//for id end of URL
	        		String [] requestURIs = requestURI.split("/");
	        		String appId = requestURIs[requestURIs.length -1];
	        		_logger.debug("appId {}",appId);
		        	app = appsService.getById(appId);
	        	}else if(requestURI.contains("/authz/oauth/v20/authorize")) {//oauth
		        	app = appsService.getByClientId(request.getParameter("client_id"),true);
	        	}
	        }
	        if(app == null) {
	        	//应用未注册
	        	_logger.debug("preHandle app is not exist . ");
	        	return true;
	        }
	        SignPrincipal principal = AuthorizationUtils.getPrincipal();
			//鉴权应用
			if (permissionAuthenticationService.authApp(principal.getUserInfo().getId(),app.getId())) {
				//访问授权应用
				_logger.trace("preHandle have authority access {}" , app);
				return true;
			}
	        //访问未授权应用，跳转到提示
	        _logger.debug("preHandle not have authority access {}" , app);
	        response.sendRedirect(request.getContextPath()+"/authz/refused");
	        return false;
    	}
        return true;
    }

}
