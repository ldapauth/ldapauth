package com.ldapauth.authz.singlelogout;

import java.util.HashMap;
import java.util.UUID;

import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.pojo.entity.client.Client;
import com.ldapauth.util.DateUtils;
import org.springframework.security.core.Authentication;

public class DefaultSingleLogout extends SingleLogout{

    @Override
    public void sendRequest(Authentication authentication, Client logoutApp) {
        HashMap<String,Object> logoutParameters  = new HashMap<String,Object>();
        logoutParameters.put("id",  UUID.randomUUID().toString());
        logoutParameters.put("principal", authentication.getName());
        logoutParameters.put("request",  "logoutRequest");
        logoutParameters.put("issueInstant", DateUtils.getCurrentDateAsString(DateUtils.FORMAT_DATE_ISO_TIMESTAMP));
        logoutParameters.put("ticket",  ((SignPrincipal)authentication.getPrincipal()).getSession().getFormattedId());
        postMessage(logoutApp.getLogoutUri(),logoutParameters);

    }

}
