package com.ldapauth.authz.cas.endpoint.adapter;

import com.ldapauth.authz.cas.endpoint.response.ServiceResponseBuilder;
import com.ldapauth.authz.endpoint.adapter.AbstractAuthorizeAdapter;
import com.ldapauth.pojo.entity.client.details.ClientCASDetails;
import com.ldapauth.web.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

public class CasPlainAdapter extends AbstractAuthorizeAdapter {
	final static Logger _logger = LoggerFactory.getLogger(CasPlainAdapter.class);

	ServiceResponseBuilder serviceResponseBuilder;

	ClientCASDetails appsCasDetails;

	@Override
	public ModelAndView authorize(ModelAndView modelAndView) {

		return modelAndView;
	}
	public CasPlainAdapter() {

	}

	public CasPlainAdapter(ClientCASDetails appsCasDetails) {
		this.appsCasDetails = appsCasDetails;
	}
	@Override
	public Object generateInfo() {
		//user for return
		String user = getValueByUserAttr(userInfo,appsCasDetails.getSubject());
		_logger.debug("cas user {}",user);
		serviceResponseBuilder.success().setUser(user);

		//for user
		serviceResponseBuilder.setAttribute("uid",String.valueOf(userInfo.getId()));
		serviceResponseBuilder.setAttribute("username", userInfo.getUsername());
		serviceResponseBuilder.setAttribute("displayName", userInfo.getDisplayName());
		serviceResponseBuilder.setAttribute("nickName", userInfo.getNickName());
		serviceResponseBuilder.setAttribute("mobile", userInfo.getMobile());
		serviceResponseBuilder.setAttribute("gender", userInfo.getGender()+"");

		//for work
		serviceResponseBuilder.setAttribute("email", userInfo.getEmail());
		serviceResponseBuilder.setAttribute("departmentId",String.valueOf(userInfo.getDepartmentId()));
		serviceResponseBuilder.setAttribute(WebConstants.ONLINE_TICKET_NAME,principal.getSession().getFormattedId());
		return serviceResponseBuilder;
	}

	public void setServiceResponseBuilder(ServiceResponseBuilder serviceResponseBuilder) {
		this.serviceResponseBuilder = serviceResponseBuilder;
	}

}
