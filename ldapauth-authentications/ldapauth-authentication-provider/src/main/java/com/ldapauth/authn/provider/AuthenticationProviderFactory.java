
package com.ldapauth.authn.provider;

import java.util.concurrent.ConcurrentHashMap;

import com.ldapauth.authn.LoginCredential;
import org.springframework.security.core.Authentication;

/**
 * 认证提供者工厂
 *
 * @author Crystal.Sea
 *
 */
public class AuthenticationProviderFactory extends AbstractAuthenticationProvider {

    private  static ConcurrentHashMap<String,AbstractAuthenticationProvider> providers =
    									new ConcurrentHashMap<String,AbstractAuthenticationProvider>();

    /**
     * 登录传入类型AuthType，读取认证提供者，进行登录认证
     */
    @Override
    public Authentication authenticate(LoginCredential credential){
    	if(credential.getAuthType().equalsIgnoreCase("trusted")) {
    		//risk remove 前端不能使用trusted认证提供者登录
    		return null;
    	}
    	AbstractAuthenticationProvider provider = providers.get(credential.getAuthType() + PROVIDER_SUFFIX);

    	return provider == null ? null : provider.doAuthenticate(credential);
    }

    /**
     * trusted 认证提供者
     */
    @Override
    public Authentication authenticate(LoginCredential credential,boolean trusted){
    	AbstractAuthenticationProvider provider = providers.get(AuthType.TRUSTED + PROVIDER_SUFFIX);
    	return provider.doAuthenticate(credential);
    }

    /**
     * 增加认证提供者
     * @param provider
     */
    public void addAuthenticationProvider(AbstractAuthenticationProvider provider) {
    	if(provider != null && provider.isSupported()) {
    		providers.put(provider.getProviderName(), provider);
    	}
    }

	@Override
	public String getProviderName() {
		return "AuthenticationProviderFactory";
	}

	@Override
	public Authentication doAuthenticate(LoginCredential authentication) {
		//AuthenticationProvider Factory do nothing
		return null;
	}
}
