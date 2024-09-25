package com.ldapauth.authz.oauth2.provider.client;

import com.ldapauth.authz.oauth2.domain.ClientDetails;
import com.ldapauth.authz.oauth2.domain.client.BaseClientDetails;
import com.ldapauth.authz.oauth2.provider.ClientDetailsService;
import com.ldapauth.authz.oauth2.provider.ClientRegistrationService;
import com.ldapauth.persistence.service.ClientAppsService;
import com.ldapauth.pojo.entity.apps.ClientApps;

/**
 * Basic, Rest implementation of the client details service.
 */
public class RestClientDetailsService implements ClientDetailsService, ClientRegistrationService{

    ClientAppsService appsService;

    public RestClientDetailsService(ClientAppsService appsService) {
        this.appsService = appsService;
    }

    public ClientDetails loadClientByClientId(String clientId, boolean cached) {
        // cache in memory
        ClientApps apps = appsService.getByClientId(clientId,true);
        BaseClientDetails detailss = new BaseClientDetails(
                apps.getClientId(),
                null,
                "all",
                "all",
                "all",
                null);
        detailss.setClientSecret(apps.getClientSecret());
        return detailss;
    }
}
