package com.ldapauth.authz.saml20.binding.impl;

import java.security.KeyStore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ldapauth.crypto.keystore.KeyStoreLoader;
import com.ldapauth.crypto.keystore.KeyStoreUtil;
import com.ldapauth.pojo.entity.client.details.ClientSAMLDetails;
import org.apache.commons.lang.Validate;
import org.apache.velocity.app.VelocityEngine;
import com.ldapauth.authz.saml.common.AuthnRequestInfo;
import com.ldapauth.authz.saml.common.TrustResolver;
import com.ldapauth.authz.saml20.binding.BindingAdapter;
import com.ldapauth.authz.saml20.binding.ExtractBindingAdapter;
import org.opensaml.common.SignableSAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.common.binding.encoding.SAMLMessageEncoder;
import org.opensaml.saml2.binding.encoding.HTTPPostEncoder;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.security.SecurityPolicyResolver;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.credential.KeyStoreCredentialResolver;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class PostBindingAdapter implements BindingAdapter, InitializingBean{
	private final static Logger logger = LoggerFactory.getLogger(PostBindingAdapter.class);

	static final String SAML_REQUEST_POST_PARAM_NAME = "SAMLRequest";
	static final String SAML_RESPONSE_POST_PARAM_NAME = "SAMLResponse";

	protected VelocityEngine velocityEngine;

	protected SAMLMessageEncoder encoder;
	protected  String issuerEntityName;

	protected CredentialResolver credentialResolver;
	protected Credential signingCredential;
	protected Credential spSigningCredential;
	protected SecurityPolicyResolver securityPolicyResolver;

	protected ExtractBindingAdapter extractBindingAdapter;

	protected AuthnRequestInfo authnRequestInfo;

	protected String relayState;

	public PostBindingAdapter() {
		super();
	}

	public PostBindingAdapter(SAMLMessageDecoder decoder,String issuerEntityName) {
		super();
		this.issuerEntityName = issuerEntityName;
	}

	public PostBindingAdapter(String issuerEntityName, SecurityPolicyResolver securityPolicyResolver) {
		super();
		this.issuerEntityName = issuerEntityName;

		this.securityPolicyResolver = securityPolicyResolver;
	}


	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendSAMLMessage(SignableSAMLObject samlMessage,
								Endpoint endpoint,
								HttpServletRequest request,
								HttpServletResponse response) throws MessageEncodingException {

		HttpServletResponseAdapter outTransport = new HttpServletResponseAdapter(response, false);

		BasicSAMLMessageContext messageContext = new BasicSAMLMessageContext();

		if (relayState!=null) {
			messageContext.setRelayState(relayState);
		}

		messageContext.setOutboundMessageTransport(outTransport);
		messageContext.setPeerEntityEndpoint(endpoint);
		messageContext.setOutboundSAMLMessage(samlMessage);
		messageContext.setOutboundMessageIssuer(issuerEntityName);
		messageContext.setOutboundSAMLMessageSigningCredential(signingCredential);

		encoder.encode(messageContext);

	}


	public void  buildCredentialResolver(CredentialResolver credentialResolver) throws Exception{
		this.credentialResolver=credentialResolver;
		CriteriaSet criteriaSet = new CriteriaSet();
		criteriaSet.add(new EntityIDCriteria(getKeyStoreLoader().getEntityName()));
		criteriaSet.add(new UsageCriteria(UsageType.SIGNING));

		try {
			signingCredential = credentialResolver.resolveSingle(criteriaSet);
		} catch (SecurityException e) {
			logger.error("Credential Resolver error . ", e);
			throw new Exception(e);
		}
		Validate.notNull(signingCredential);
	}

	public Credential  buildSPSigningCredential() throws Exception{
		KeyStore trustKeyStore = KeyStoreUtil.bytes2KeyStore(getSaml20Details().getKeystore(),
				getKeyStoreLoader().getKeyStore().getType(),
				getKeyStoreLoader().getKeystorePassword());

		TrustResolver trustResolver=new TrustResolver();
		KeyStoreCredentialResolver credentialResolver =trustResolver.buildKeyStoreCredentialResolver(
							trustKeyStore,
							getSaml20Details().getEntityId(),
							getKeyStoreLoader().getKeystorePassword());

		CriteriaSet criteriaSet = new CriteriaSet();
		criteriaSet.add(new EntityIDCriteria(getSaml20Details().getEntityId()));
		criteriaSet.add(new UsageCriteria(UsageType.ENCRYPTION));

		try {
			spSigningCredential = credentialResolver.resolveSingle(criteriaSet);
		} catch (SecurityException e) {
			logger.error("Credential Resolver error . ", e);
			throw new Exception(e);
		}
		Validate.notNull(spSigningCredential);

		return spSigningCredential;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		encoder = new HTTPPostEncoder(velocityEngine,"/templates/saml2-post-binding.vm");
	}

	/**
	 * @param securityPolicyResolver the securityPolicyResolver to set
	 */
	public void setSecurityPolicyResolver(
			SecurityPolicyResolver securityPolicyResolver) {
		this.securityPolicyResolver = securityPolicyResolver;
	}



	public void setIssuerEntityName(String issuerEntityName) {
		this.issuerEntityName = issuerEntityName;
	}

	public KeyStoreLoader getKeyStoreLoader() {
		return extractBindingAdapter.getKeyStoreLoader();
	}

	public Credential getSigningCredential() {
		return signingCredential;
	}

	public void setSigningCredential(Credential signingCredential) {
		this.signingCredential = signingCredential;
	}

	public Credential getSpSigningCredential() {
		return spSigningCredential;
	}

	public void setSpSigningCredential(Credential spSigningCredential) {
		this.spSigningCredential = spSigningCredential;
	}

	public AuthnRequestInfo getAuthnRequestInfo() {
		return authnRequestInfo;
	}

	public void setAuthnRequestInfo(AuthnRequestInfo authnRequestInfo) {
		this.authnRequestInfo = authnRequestInfo;
	}

	public void setRelayState(String relayState) {
		this.relayState = relayState;
	}

	@Override
	public void setExtractBindingAdapter(
			ExtractBindingAdapter extractBindingAdapter) {
		this.extractBindingAdapter=extractBindingAdapter;
		this.credentialResolver=extractBindingAdapter.getCredentialResolver();
		try {
			buildCredentialResolver(extractBindingAdapter.getCredentialResolver());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public ClientSAMLDetails getSaml20Details() {
		return extractBindingAdapter.getSaml20Detail();
	}
}
