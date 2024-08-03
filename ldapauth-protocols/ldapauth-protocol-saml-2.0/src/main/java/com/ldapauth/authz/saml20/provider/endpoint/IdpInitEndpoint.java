package com.ldapauth.authz.saml20.provider.endpoint;

import java.security.KeyStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ldapauth.authz.saml.common.AuthnRequestInfo;
import com.ldapauth.authz.saml20.binding.BindingAdapter;
import com.ldapauth.authz.saml20.binding.ExtractBindingAdapter;
import com.ldapauth.crypto.keystore.KeyStoreLoader;
import com.ldapauth.crypto.keystore.KeyStoreUtil;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.persistence.service.AppsService;
import com.ldapauth.pojo.dto.AppsDetails;
import com.ldapauth.pojo.entity.apps.Apps;
import com.ldapauth.pojo.entity.apps.details.AppsSamlDetails;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * idp init  not need extract SAML request message
 * AuthnRequestInfo use default init
 * @author Crystal.Sea
 *
 */
@Tag(name = "2-2-SAML v2.0 API文档模块")
@Controller
public class IdpInitEndpoint {
	private final static Logger logger = LoggerFactory.getLogger(IdpInitEndpoint.class);

	private BindingAdapter bindingAdapter;

	@Autowired
	@Qualifier("postSimpleSignBindingAdapter")
	private BindingAdapter postSimpleSignBindingAdapter;

	@Autowired
	@Qualifier("postBindingAdapter")
	private BindingAdapter postBindingAdapter;

	@Autowired
	@Qualifier("extractRedirectBindingAdapter")
	private ExtractBindingAdapter extractRedirectBindingAdapter;

	@Autowired
	@Qualifier("keyStoreLoader")
	private KeyStoreLoader keyStoreLoader;

	@Autowired
	private AppsService appsService;

	/**
	 *
	 * @param request
	 * @param response
	 * @param appId
	 * @return
	 * @throws Exception
	 *
	 *
	 */
	@Operation(summary = "SAML 2.0 IDP Init接口", description = "传递参数应用ID",method="GET")
	@RequestMapping(value = "/auth/saml20/idpinit/{appid}", method=RequestMethod.GET)
	public ModelAndView authorizeIdpInit(HttpServletRequest request,
										 HttpServletResponse response,
										 @PathVariable("appid") String appId)throws Exception {
		logger.debug("SAML IDP init , app id is "+appId);

		Result<AppsDetails> details = appsService.getDetails(Long.valueOf(appId));
		Result<AppsDetails> result = details;
		AppsSamlDetails saml20Details =null;
		Apps apps =null;
		if (result.isSuccess()) {
			AppsDetails d = result.getData();
			saml20Details = (AppsSamlDetails) d.getDetails();
			apps = d.getApp();
		}
		if (saml20Details == null) {
			logger.error("samlId[" + appId + "] 不存在 .");
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"SAML应用"+appId+"不存在");
		}
		if (apps == null) {
			logger.error("samlId[" + appId + "] 不存在 .");
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"SAML应用"+appId+"不存在");
		}

		if (saml20Details.getStatus().intValue() != 0) {
			logger.error("samlId[" + appId + "] 被禁用 .");
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"SAML应用"+appId+"被禁用");
		}
		WebContext.setAttribute(WebConstants.AUTHORIZE_SIGN_ON_APP, apps);

		KeyStore trustKeyStore = KeyStoreUtil.bytes2KeyStore(saml20Details.getKeystore(),
				keyStoreLoader.getKeyStore().getType(),
				keyStoreLoader.getKeystorePassword());

		extractRedirectBindingAdapter.setSaml20Detail(saml20Details);
		extractRedirectBindingAdapter.buildSecurityPolicyResolver(trustKeyStore);

		String binding=saml20Details.getBinding();

		if(binding.endsWith("PostSimpleSign")){
			bindingAdapter=postSimpleSignBindingAdapter;
		}else{
			bindingAdapter=postBindingAdapter;
		}

		//AuthnRequestInfo init authnRequestID to null
		bindingAdapter.setAuthnRequestInfo(new AuthnRequestInfo());

		bindingAdapter.setExtractBindingAdapter(extractRedirectBindingAdapter);

		request.getSession().setAttribute(WebConstants.AUTHORIZE_SIGN_ON_APP_SAMLV20_ADAPTER, bindingAdapter);

		logger.debug("idp init forwarding to assertion :","/auth/saml20/v1/assertion");

		return WebContext.forward("/auth/saml20/v1/assertion");
	}


	/**
	 * @param keyStoreLoader
	 *            the keyStoreLoader to set
	 */
	public void setKeyStoreLoader(KeyStoreLoader keyStoreLoader) {
		this.keyStoreLoader = keyStoreLoader;
	}

}
