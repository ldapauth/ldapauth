package com.ldapauth.authz.oauth2.provider.client;

import java.util.Map;
import com.ldapauth.authz.oauth2.domain.ClientDetails;
import com.ldapauth.authz.oauth2.domain.client.BaseClientDetails;
import com.ldapauth.authz.oauth2.provider.ClientDetailsService;
import com.ldapauth.authz.oauth2.provider.ClientRegistrationService;
import com.ldapauth.authz.oauth2.provider.NoSuchClientException;
import com.ldapauth.persistence.service.AppsService;
import com.ldapauth.pojo.dto.AppsDetails;
import com.ldapauth.pojo.entity.apps.Apps;
import com.ldapauth.pojo.entity.apps.details.AppsOidcDetails;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.StringUtils;

/**
 * Basic, JDBC implementation of the client details service.
 */
public class JdbcClientDetailsService implements ClientDetailsService, ClientRegistrationService{

    AppsService appsService;

    public JdbcClientDetailsService(AppsService appsService) {
        this.appsService = appsService;
    }

    public ClientDetails loadClientByClientId(String clientId, boolean cached) {
        // cache in memory
        AppsDetails<AppsOidcDetails> detailsAppsDetails = appsService.getDetailsClientId(clientId);
        Apps apps = detailsAppsDetails.getApp();
        AppsOidcDetails appsOidcDetails = detailsAppsDetails.getDetails();
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
