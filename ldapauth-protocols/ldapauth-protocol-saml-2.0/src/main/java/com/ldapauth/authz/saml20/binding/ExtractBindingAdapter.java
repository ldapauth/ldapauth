package com.ldapauth.authz.saml20.binding;

import java.security.KeyStore;

import javax.servlet.http.HttpServletRequest;

import com.ldapauth.crypto.keystore.KeyStoreLoader;
import com.ldapauth.pojo.entity.apps.details.AppsSamlDetails;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.security.SecurityPolicyResolver;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.CredentialResolver;

/**
 *
 * Abstracts the SAML Binding used to send/receive messages.
 *
 *
 */
public interface ExtractBindingAdapter {

	@SuppressWarnings("rawtypes")
	public SAMLMessageContext extractSAMLMessageContext(HttpServletRequest request) throws MessageDecodingException, SecurityException;

	public String extractSAMLMessage(HttpServletRequest request);

	public void setSecurityPolicyResolver(SecurityPolicyResolver securityPolicyResolver);

	public void buildSecurityPolicyResolver(KeyStore trustKeyStore);

	public void setSaml20Detail(AppsSamlDetails saml20Detail);

	public AppsSamlDetails getSaml20Detail();

	public KeyStoreLoader getKeyStoreLoader();

	public CredentialResolver getCredentialResolver();
}
