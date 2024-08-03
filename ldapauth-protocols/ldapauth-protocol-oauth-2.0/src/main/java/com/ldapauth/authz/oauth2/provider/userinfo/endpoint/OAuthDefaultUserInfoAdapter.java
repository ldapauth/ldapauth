package com.ldapauth.authz.oauth2.provider.userinfo.endpoint;

import java.util.HashMap;

import com.ldapauth.authz.endpoint.adapter.AbstractAuthorizeAdapter;
import com.ldapauth.authz.oauth2.domain.ClientDetails;
import com.ldapauth.util.JsonUtils;
import com.ldapauth.util.StringGenerator;
import com.ldapauth.web.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthDefaultUserInfoAdapter extends AbstractAuthorizeAdapter {
	final static Logger _logger = LoggerFactory.getLogger(OAuthDefaultUserInfoAdapter.class);
	ClientDetails clientDetails;

	public OAuthDefaultUserInfoAdapter() {}

	public OAuthDefaultUserInfoAdapter(ClientDetails clientDetails) {
		this.clientDetails = clientDetails;
	}

	@Override
	public Object generateInfo() {
		 String subject = AbstractAuthorizeAdapter.getValueByUserAttr(userInfo, clientDetails.getSubject());
		 _logger.debug("userId : {} , username : {} , displayName : {} , subject : {}" ,
				 userInfo.getId(),
				 userInfo.getUsername(),
				 userInfo.getDisplayName(),
				 subject);

		HashMap<String, Object> beanMap = new HashMap<String, Object>();
		beanMap.put("randomId",(new StringGenerator()).uuidGenerate());
		beanMap.put("id", userInfo.getId());
		beanMap.put("userId", userInfo.getId());
		//for spring security oauth2
		beanMap.put("user", subject);
		beanMap.put("username", subject);
		beanMap.put("displayName", userInfo.getDisplayName());
		beanMap.put("email", userInfo.getEmail());
		beanMap.put("mobile", userInfo.getMobile());
		beanMap.put("realname", userInfo.getDisplayName());
		beanMap.put("birthday", userInfo.getBirthDate());
		beanMap.put("departmentId", userInfo.getDepartmentId());
		beanMap.put("gender", userInfo.getGender());
		beanMap.put(WebConstants.ONLINE_TICKET_NAME, principal.getSession().getFormattedId());

		String info= JsonUtils.toString(beanMap);

		return info;
	}

	public ClientDetails getClientDetails() {
		return clientDetails;
	}

	public void setClientDetails(ClientDetails clientDetails) {
		this.clientDetails = clientDetails;
	}
}
