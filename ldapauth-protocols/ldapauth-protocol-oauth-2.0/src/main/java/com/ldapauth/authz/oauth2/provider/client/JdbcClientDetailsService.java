package com.ldapauth.authz.oauth2.provider.client;

import com.ldapauth.authz.oauth2.domain.ClientDetails;
import com.ldapauth.authz.oauth2.domain.client.BaseClientDetails;
import com.ldapauth.authz.oauth2.provider.ClientDetailsService;
import com.ldapauth.authz.oauth2.provider.ClientRegistrationService;
import com.ldapauth.persistence.service.ClientAppsService;
import com.ldapauth.pojo.dto.AppsDetails;
import com.ldapauth.pojo.entity.apps.ClientApps;
import com.ldapauth.pojo.entity.apps.details.ClientAppsOIDCDetails;
import org.springframework.util.StringUtils;

/**
 * Basic, JDBC implementation of the client details service.
 */
public class JdbcClientDetailsService implements ClientDetailsService, ClientRegistrationService{

    ClientAppsService appsService;

    public JdbcClientDetailsService(ClientAppsService appsService) {
        this.appsService = appsService;
    }

    public ClientDetails loadClientByClientId(String clientId, boolean cached) {
        // cache in memory
        AppsDetails<ClientAppsOIDCDetails> detailsAppsDetails = appsService.getDetailsClientId(clientId);
        ClientApps apps = detailsAppsDetails.getApp();
        ClientAppsOIDCDetails appsOidcDetails = detailsAppsDetails.getDetails();
        BaseClientDetails detailss = new BaseClientDetails(
                apps.getClientId(),
                null,
                appsOidcDetails.getScope(),
                appsOidcDetails.getAuthorizedGrantTypes(),
                appsOidcDetails.getAuthorizedGrantTypes(),
                appsOidcDetails.getRedirectUri());
        detailss.setClientSecret(appsOidcDetails.getClientSecret());
        detailss.setAccessTokenValiditySeconds(appsOidcDetails.getAccessTokenValidity().intValue());
        detailss.setRefreshTokenValiditySeconds(appsOidcDetails.getRefreshTokenValidity().intValue());
        detailss.setAlgorithm(appsOidcDetails.getAlgorithm());
        detailss.setAlgorithmKey(appsOidcDetails.getAlgorithmKey());
        detailss.setEncryptionMethod(appsOidcDetails.getAlgorithmMethod());
        detailss.setSignature(appsOidcDetails.getSignature());
        detailss.setSignatureKey(appsOidcDetails.getSignatureKey());
        detailss.setSubject(appsOidcDetails.getSubject());
        detailss.setAudience(appsOidcDetails.getIssuer());
        detailss.setIssuer(appsOidcDetails.getIssuer());
        detailss.setAppId(apps.getId());
        detailss.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(appsOidcDetails.getScope()));
        return detailss;
    }
}
