package com.ldapauth.authz.jwt.endpoint.adapter;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import com.ldapauth.authz.endpoint.adapter.AbstractAuthorizeAdapter;
import com.ldapauth.constants.IDPCacheConstants;
import com.ldapauth.crypto.jwt.encryption.service.impl.DefaultJwtEncryptionAndDecryptionService;
import com.ldapauth.crypto.jwt.signer.service.impl.DefaultJwtSigningAndValidationService;
import com.ldapauth.pojo.entity.apps.details.AppsJwtDetails;
import com.ldapauth.web.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;

@Slf4j
public class JwtAdapter extends AbstractAuthorizeAdapter {
	final static Logger _logger = LoggerFactory.getLogger(JwtAdapter.class);

	AppsJwtDetails jwtDetails;

	JWT jwtToken;

	JWEObject jweObject;

	JWTClaimsSet jwtClaims;


	public JwtAdapter() {

	}

	public JwtAdapter(AppsJwtDetails jwtDetails) {
		this.jwtDetails = jwtDetails;
	}

	@Override
	public Object generateInfo() {
		DateTime currentDateTime = DateTime.now();
		Date expirationTime = currentDateTime.plusSeconds(jwtDetails.getIdTokenValidity().intValue()).toDate();
		_logger.debug("expiration Time : {}" , expirationTime);
		String subject = getValueByUserAttr(userInfo,jwtDetails.getSubject());
		_logger.trace("jwt subject : {}" , subject);

		jwtClaims =new  JWTClaimsSet.Builder()
				.issuer(jwtDetails.getIssuer())
				.subject(subject)
				.audience(Arrays.asList(String.valueOf(jwtDetails.getAppId())))
				.jwtID(UUID.randomUUID().toString())
				.issueTime(currentDateTime.toDate())
				.expirationTime(expirationTime)
				.claim("email", userInfo.getEmail())
				.claim("name", userInfo.getUsername())
				.claim("mobile", userInfo.getMobile())
				.claim("user_id", userInfo.getId())
				.claim("display_name", userInfo.getDisplayName())
				.claim("external_id", userInfo.getId())
				.claim(WebConstants.ONLINE_TICKET_NAME, principal.getSession().getFormattedId())
				.claim("kid", jwtDetails.getAppId()+ "_sig")
				.build();

		_logger.trace("jwt Claims : {}" , jwtClaims);

		jwtToken = new PlainJWT(jwtClaims);

		return jwtToken;
	}

	@Override
	public Object sign(Object data) {
		if(jwtDetails.getIsSignature().equalsIgnoreCase(IDPCacheConstants.YES)) {
			if (!jwtDetails.getSignature().equalsIgnoreCase("none")) {
				try {
					DefaultJwtSigningAndValidationService jwtSignerService =
							new DefaultJwtSigningAndValidationService(
									jwtDetails.getSignatureKey(),
									jwtDetails.getAppId() + "_sig",
									jwtDetails.getSignature()
							);
					jwtToken = new SignedJWT(
							new JWSHeader(jwtSignerService.getDefaultSigningAlgorithm()),
							jwtClaims
					);
					// sign it with the server's key
					jwtSignerService.signJwt((SignedJWT) jwtToken);
					return jwtToken;
				} catch (NoSuchAlgorithmException e) {
					log.error("NoSuchAlgorithmException", e);
				} catch (InvalidKeySpecException e) {
					log.error("InvalidKeySpecException", e);
				} catch (JOSEException e) {
					log.error("JOSEException", e);
				}
			}
		}
		return data;
	}

	@Override
	public Object encrypt(Object data) {
		if(jwtDetails.getIsAlgorithm().equalsIgnoreCase(IDPCacheConstants.YES)) {
			if (!jwtDetails.getAlgorithm().equalsIgnoreCase("none")) {
				try {
					DefaultJwtEncryptionAndDecryptionService jwtEncryptionService =
							new DefaultJwtEncryptionAndDecryptionService(
									jwtDetails.getAlgorithmKey(),
									jwtDetails.getAppId() + "_enc",
									jwtDetails.getAlgorithm()
							);

					Payload payload;
					if (jwtToken instanceof SignedJWT) {
						payload = ((SignedJWT) jwtToken).getPayload();
					} else {
						payload = ((PlainJWT) jwtToken).getPayload();
					}
					// Example Request JWT encrypted with RSA-OAEP-256 and 128-bit AES/GCM
					//JWEHeader jweHeader = new JWEHeader(JWEAlgorithm.RSA1_5, EncryptionMethod.A128GCM);
					JWEHeader jweHeader = new JWEHeader(
							jwtEncryptionService.getDefaultAlgorithm(jwtDetails.getAlgorithm()),
							jwtEncryptionService.parseEncryptionMethod(jwtDetails.getAlgorithmMethod())
					);
					jweObject = new JWEObject(
							new JWEHeader.Builder(jweHeader)
									.contentType("JWT") // required to indicate nested JWT
									.build(),
							payload);

					jwtEncryptionService.encryptJwt(jweObject);

				} catch (NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {

				}
			}
		}
		return data;
	}

	@Override
	public ModelAndView authorize(ModelAndView modelAndView) {
		modelAndView.setViewName("authorize/jwt_sso_submint");
		modelAndView.addObject("action", jwtDetails.getRedirectUri());

		modelAndView.addObject("token",serialize());
		modelAndView.addObject("jwtName",jwtDetails.getJwtName());

		modelAndView.addObject("tokenType",jwtDetails.getSsoBinding().toLowerCase());

		return modelAndView;
	}

	public void setJwtDetails(AppsJwtDetails jwtDetails) {
		this.jwtDetails = jwtDetails;
	}

	@Override
	public String serialize() {
		String tokenString = "";
		if(jweObject != null) {
			tokenString = jweObject.serialize();
		}else {
			tokenString = jwtToken.serialize();
		}
		_logger.debug("jwt Token : {}" , tokenString);
		return tokenString;
	}

}
