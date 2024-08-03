package com.ldapauth.authz.oauth2.provider.userinfo.endpoint;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authz.endpoint.adapter.AbstractAuthorizeAdapter;
import com.ldapauth.authz.oauth2.domain.ClientDetails;
import com.ldapauth.persistence.service.AppsService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.util.JsonUtils;
import com.ldapauth.util.RequestTokenUtils;
import com.ldapauth.util.StringGenerator;
import com.ldapauth.web.HttpResponseAdapter;
import com.ldapauth.authz.oauth2.common.OAuth2Constants;
import com.ldapauth.authz.oauth2.common.exceptions.OAuth2Exception;
import com.ldapauth.authz.oauth2.provider.ClientDetailsService;
import com.ldapauth.authz.oauth2.provider.OAuth2Authentication;
import com.ldapauth.authz.oauth2.provider.token.DefaultTokenServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "2-1-OAuth v2.0 API文档模块")
@Controller
public class UserInfoEndpoint {

	final static Logger _logger = LoggerFactory.getLogger(UserInfoEndpoint.class);

	@Autowired
	@Qualifier("oauth20JdbcClientDetailsService")
	private ClientDetailsService clientDetailsService;

	@Autowired
	@Qualifier("oauth20TokenServices")
	private DefaultTokenServices oauth20tokenServices;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	protected AppsService appsService;

    @Autowired
    protected HttpResponseAdapter httpResponseAdapter;

    @Operation(summary = "OAuth 2.0 用户信息接口", description = "请求参数access_token , header Authorization , token ",method="POST,GET")
	@RequestMapping(value=OAuth2Constants.ENDPOINT.ENDPOINT_USERINFO, method={RequestMethod.POST, RequestMethod.GET})
	public void apiV20UserInfo(
            HttpServletRequest request,
            HttpServletResponse response) {
    		String access_token =  RequestTokenUtils.resolveAccessToken(request);

			if (!StringGenerator.uuidMatches(access_token)) {
				httpResponseAdapter.write(response, JsonUtils.gsonToString(accessTokenFormatError(access_token)),"json");
			}

			OAuth2Authentication oAuth2Authentication =null;
			try{
				 oAuth2Authentication = oauth20tokenServices.loadAuthentication(access_token);

				 String client_id= oAuth2Authentication.getOAuth2Request().getClientId();
				 ClientDetails clientDetails =
						 clientDetailsService.loadClientByClientId(client_id,true);
				AbstractAuthorizeAdapter adapter = new OAuthDefaultUserInfoAdapter(clientDetails);
				adapter.setPrincipal((SignPrincipal)oAuth2Authentication.getUserAuthentication().getPrincipal());
				Object jsonData = adapter.generateInfo();
				httpResponseAdapter.write(response,jsonData.toString(),"json");
			}catch(OAuth2Exception e){
				HashMap<String,Object>authzException=new HashMap<String,Object>();
				authzException.put(OAuth2Exception.ERROR, e.getOAuth2ErrorCode());
				authzException.put(OAuth2Exception.DESCRIPTION,e.getMessage());
				httpResponseAdapter.write(response,JsonUtils.gsonToString(authzException),"json");
			}
	}

	public HashMap<String,Object> accessTokenFormatError(String access_token){
		HashMap<String,Object>atfe=new HashMap<String,Object>();
		atfe.put(OAuth2Exception.ERROR, "token Format Invalid");
		atfe.put(OAuth2Exception.DESCRIPTION, "access Token Format Invalid , access_token : "+access_token);

		return atfe;
	}


	public void setOauth20tokenServices(DefaultTokenServices oauth20tokenServices) {
		this.oauth20tokenServices = oauth20tokenServices;
	}



	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

}
