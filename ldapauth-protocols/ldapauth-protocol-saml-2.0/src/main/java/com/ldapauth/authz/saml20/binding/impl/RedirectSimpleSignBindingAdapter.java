
package com.ldapauth.authz.saml20.binding.impl;

import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.ws.security.SecurityPolicyResolver;

public class RedirectSimpleSignBindingAdapter extends PostBindingAdapter{

	public RedirectSimpleSignBindingAdapter() {
		super();
	}

	public RedirectSimpleSignBindingAdapter(String issuerEntityName) {
		super();
		this.issuerEntityName = issuerEntityName;
	}

	public RedirectSimpleSignBindingAdapter(String issuerEntityName, SecurityPolicyResolver securityPolicyResolver) {
		super();
		this.issuerEntityName = issuerEntityName;

		this.securityPolicyResolver = securityPolicyResolver;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		encoder = new HTTPRedirectDeflateEncoder();
	}




}
