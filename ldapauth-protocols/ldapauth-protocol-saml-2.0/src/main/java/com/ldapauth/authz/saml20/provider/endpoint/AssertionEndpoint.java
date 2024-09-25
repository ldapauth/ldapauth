package com.ldapauth.authz.saml20.provider.endpoint;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.authz.saml.common.AuthnRequestInfo;
import com.ldapauth.authz.saml.common.EndpointGenerator;
import com.ldapauth.authz.saml20.binding.BindingAdapter;
import com.ldapauth.authz.saml20.provider.xml.AuthnResponseGenerator;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.entity.apps.details.ClientAppsSAMLDetails;
import com.ldapauth.web.WebConstants;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class AssertionEndpoint {
	private final static Logger logger = LoggerFactory.getLogger(AssertionEndpoint.class);

	private BindingAdapter bindingAdapter;

	@Autowired
	@Qualifier("endpointGenerator")
	EndpointGenerator endpointGenerator;

	@Autowired
	@Qualifier("authnResponseGenerator")
	AuthnResponseGenerator authnResponseGenerator;

	@RequestMapping(value = "/auth/saml20/v1/assertion", method= {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView assertion(	HttpServletRequest request,
									HttpServletResponse response
									) throws Exception {
		logger.debug("saml20 assertion start.");
		bindingAdapter = (BindingAdapter) request.getSession().getAttribute(
		        WebConstants.AUTHORIZE_SIGN_ON_APP_SAMLV20_ADAPTER);
		logger.debug("saml20 assertion get session samlv20Adapter "+bindingAdapter);
		ClientAppsSAMLDetails saml20Details = bindingAdapter.getSaml20Details();
		AuthnRequestInfo authnRequestInfo = bindingAdapter.getAuthnRequestInfo();

		if (authnRequestInfo == null) {
			logger.warn("Could not find AuthnRequest on the request.  Responding with SC_FORBIDDEN.");
			throw new Exception();
		}

		logger.debug("AuthnRequestInfo: {}", authnRequestInfo);
		HashMap <String,String>attributeMap=new HashMap<String,String>();
		attributeMap.put(WebConstants.ONLINE_TICKET_NAME,
		        AuthorizationUtils.getPrincipal().getSession().getFormattedId());

		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		//saml20Details
		Response authResponse = authnResponseGenerator.generateAuthnResponse(
				saml20Details,
				authnRequestInfo,
				attributeMap,
				bindingAdapter,
				currentUser);

		Endpoint endpoint = endpointGenerator.generateEndpoint(saml20Details.getSpAcsUri());

		request.getSession().removeAttribute(AuthnRequestInfo.class.getName());

		// we could use a different adapter to send the response based on
		// request issuer...
		try {
			bindingAdapter.sendSAMLMessage(authResponse, endpoint, request,response);
		} catch (MessageEncodingException mee) {
			logger.error("Exception encoding SAML message", mee);
			throw new Exception(mee);
		}
		return null;
	}



}
