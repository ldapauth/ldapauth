package com.ldapauth.authz.endpoint;

import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.persistence.service.AppsService;
import com.ldapauth.pojo.entity.apps.Apps;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @author Crystal.Sea
 *
 */
public class AuthorizeBaseEndpoint {
	final static Logger _logger = LoggerFactory.getLogger(AuthorizeBaseEndpoint.class);

	@Autowired
    protected ApplicationConfig applicationConfig;

	@Autowired
	protected AppsService appsService;

	protected Apps getApp(Long id){
		Apps  app= (Apps) WebContext.getAttribute(WebConstants.AUTHORIZE_SIGN_ON_APP);
		if(Objects.isNull(id)) {
			_logger.error("parameter for app id " + id + "  is null.");
		}else {
			//session中为空或者id不一致重新加载
			if(app == null || app.getId().longValue() != id.longValue()) {
				app = appsService.getById(id);
			}
			WebContext.setAttribute(WebConstants.AUTHORIZE_SIGN_ON_APP,app);
		}
		if(app	==	null){
			_logger.error("Applications id " + id + "  is not exist.");
		}
		return app;
	}
}
