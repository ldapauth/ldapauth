package com.ldapauth.authz.endpoint.adapter;

import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.entity.apps.details.AppsCasDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractAuthorizeAdapter {


	protected UserInfo userInfo;

	protected SignPrincipal principal;

	public abstract Object generateInfo();

	public  ModelAndView authorize(ModelAndView modelAndView) {
		return modelAndView;
	}

	public Object sign(Object data){
		return null;
	}

	public Object encrypt(Object data){
		return null;
	}

	public static String getValueByUserAttr(UserInfo userInfo,String userAttr) {
		String value = "";
		if(StringUtils.isBlank(userAttr)) {
			value = userInfo.getUsername();
		}else if(userAttr.equalsIgnoreCase("username")){
			value = userInfo.getUsername();
		}else if(userAttr.equalsIgnoreCase("userId")){
			value = String.valueOf(userInfo.getId());
		}else if(userAttr.equalsIgnoreCase("email")){
			value = userInfo.getEmail();
		}else if(userAttr.equalsIgnoreCase("mobile")){
			value = userInfo.getMobile();
		}else {
			value = String.valueOf(userInfo.getId());
		}

		if (StringUtils.isBlank(value)) {
			value = userInfo.getUsername();
		}

		return value;
	}

	public  String serialize() {
		return "";
	};

	public void setPrincipal(SignPrincipal principal) {
		this.principal = principal;
		this.userInfo = principal.getUserInfo();
	}


}
