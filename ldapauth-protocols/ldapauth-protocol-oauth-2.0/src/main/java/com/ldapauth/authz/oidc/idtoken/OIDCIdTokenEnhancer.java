package com.ldapauth.authz.oidc.idtoken;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import cn.hutool.core.bean.BeanUtil;
import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.authz.endpoint.adapter.AbstractAuthorizeAdapter;
import com.ldapauth.authz.oauth2.domain.ClientDetails;
import com.ldapauth.authz.oauth2.provider.userinfo.endpoint.OAuthDefaultUserInfoAdapter;
import com.ldapauth.configuration.oidc.OIDCProviderMetadata;
import com.ldapauth.crypto.jwt.encryption.service.impl.DefaultJwtEncryptionAndDecryptionService;
import com.ldapauth.crypto.jwt.signer.service.impl.DefaultJwtSigningAndValidationService;
import com.ldapauth.pojo.entity.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.ldapauth.authz.oauth2.common.DefaultOAuth2AccessToken;
import com.ldapauth.authz.oauth2.common.OAuth2AccessToken;
import com.ldapauth.authz.oauth2.provider.ClientDetailsService;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;
import com.ldapauth.authz.oauth2.provider.OAuth2Request;
import com.ldapauth.authz.oauth2.provider.token.TokenEnhancer;

import com.nimbusds.jose.util.Base64URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * @author Crystal.Sea
 *
 */
public class OIDCIdTokenEnhancer implements TokenEnhancer {
	private final static Logger _logger = LoggerFactory.getLogger(OIDCIdTokenEnhancer.class);

	public  final static String ID_TOKEN_SCOPE="openid";

	private OIDCProviderMetadata providerMetadata;

	private ClientDetailsService clientDetailsService;

	public void setProviderMetadata(OIDCProviderMetadata providerMetadata) {
		this.providerMetadata = providerMetadata;
	}

	public void setClientDetailsService(ClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
	}

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		OAuth2Request  request=authentication.getOAuth2Request();
		if (request.getScope().contains(ID_TOKEN_SCOPE)) {//Enhance for OpenID Connect
			ClientDetails clientDetails =
					clientDetailsService.loadClientByClientId(authentication.getOAuth2Request().getClientId(),true);

			DefaultJwtSigningAndValidationService jwtSignerService = null;
			JWSAlgorithm signingAlg = null;
			try {//jwtSignerService
				if (StringUtils.isNotBlank(clientDetails.getSignature()) && !clientDetails.getSignature().equalsIgnoreCase("none")) {
					jwtSignerService = new DefaultJwtSigningAndValidationService(
							clientDetails.getSignatureKey(),
							clientDetails.getAppId()+"_sig",
							clientDetails.getSignature()
						);

					signingAlg = jwtSignerService.getDefaultSigningAlgorithm();
				}
			}catch(Exception e) {
				_logger.error("Couldn't create Jwt Signing Service",e);
			}
			SignPrincipal signPrincipal = (SignPrincipal) authentication.getUserAuthentication().getPrincipal();
			UserInfo userInfo = signPrincipal.getUserInfo();
			String subject = AbstractAuthorizeAdapter.getValueByUserAttr(userInfo, clientDetails.getSubject());
			JWTClaimsSet.Builder builder=new JWTClaimsSet.Builder();
			builder.subject(subject)
		      .expirationTime(accessToken.getExpiration())
		      .issuer(clientDetails.getIssuer())
		      .issueTime(new Date())
		      .audience(Arrays.asList(authentication.getOAuth2Request().getClientId()))
		      .jwtID(UUID.randomUUID().toString());

			/**
			 * https://self-issued.me
			 * @see http://openid.net/specs/openid-connect-core-1_0.html#SelfIssuedDiscovery
			 *     7.  Self-Issued OpenID Provider
			 */
			if(clientDetails.getIssuer()!=null
					&& jwtSignerService != null
					&& clientDetails.getIssuer().equalsIgnoreCase("https://self-issued.me")
					){
				builder.claim("sub_jwk", jwtSignerService.getAllPublicKeys().get(jwtSignerService.getDefaultSignerKeyId()));
			}

			// if the auth time claim was explicitly requested OR if the client always wants the auth time, put it in
			if (request.getExtensions().containsKey("max_age")
					|| (request.getExtensions().containsKey("idtoken")) // parse the ID Token claims (#473) -- for now assume it could be in there
					) {
				DateTime loginDate = new DateTime( AuthorizationUtils.getUserInfo().getLastLoginTime());
				builder.claim("auth_time",  loginDate.getMillis()/1000);
			}

			String nonce = request.getRequestParameters().get("nonce");
			_logger.debug("getRequestParameters nonce {}",nonce);

			if (!Strings.isNullOrEmpty(nonce)) {
				builder.claim("nonce", nonce);
			}
			if(jwtSignerService != null) {
				SignedJWT signed = new SignedJWT(new JWSHeader(signingAlg), builder.build());
				Set<String> responseTypes = request.getResponseTypes();

				if (responseTypes.contains("token")) {
					// calculate the token hash
					Base64URL at_hash = IdTokenHashUtils.getAccessTokenHash(signingAlg, signed);
					builder.claim("at_hash", at_hash);
				}
				_logger.debug("idClaims {}",builder.build());
			}
			String idTokenString = "";
			if (StringUtils.isNotBlank(clientDetails.getSignature())
					&& !clientDetails.getSignature().equalsIgnoreCase("none")) {
				try {
					builder.claim("kid", jwtSignerService.getDefaultSignerKeyId());
					// signed ID token
					JWT idToken = new SignedJWT(new JWSHeader(signingAlg), builder.build());
					// sign it with the server's key
					jwtSignerService.signJwt((SignedJWT) idToken);
					idTokenString = idToken.serialize();
					_logger.debug("idToken {}",idTokenString);
				}catch(Exception e) {
					_logger.error("Couldn't create Jwt Signing Exception",e);
				}
			}
			accessToken = new DefaultOAuth2AccessToken(accessToken);
			if(StringUtils.isNotBlank(idTokenString)){
				accessToken.getAdditionalInformation().put("id_token", idTokenString);
			}
		}
		return accessToken;
	}

}
