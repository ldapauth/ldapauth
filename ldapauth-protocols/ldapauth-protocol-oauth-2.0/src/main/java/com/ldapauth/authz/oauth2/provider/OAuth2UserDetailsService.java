package com.ldapauth.authz.oauth2.provider;

import java.util.ArrayList;

import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authn.provider.AbstractAuthenticationProvider;
import com.ldapauth.authn.session.Session;
import com.ldapauth.persistence.repository.LoginRepository;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.web.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Dave Syer
 *
 */
public class OAuth2UserDetailsService implements UserDetailsService {
	 private static final Logger _logger =
	            LoggerFactory.getLogger(OAuth2UserDetailsService.class);

    LoginRepository loginRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserInfo userInfo;
		try {
		    userInfo = loginRepository.find(username, "");
		} catch (NoSuchClientException e) {
			throw new UsernameNotFoundException(e.getMessage(), e);
		}

		String onlineTickitId = WebConstants.ONLINE_TICKET_PREFIX + "-" + java.util.UUID.randomUUID().toString().toLowerCase();

		SignPrincipal principal = new SignPrincipal(userInfo);
		Session onlineTicket = new Session(onlineTickitId);
		//set OnlineTicket
		principal.setSession(onlineTicket);

        ArrayList<GrantedAuthority> grantedAuthoritys = loginRepository.grantAuthority(userInfo);
        principal.setAuthenticated(true);

        for(GrantedAuthority administratorsAuthority : AbstractAuthenticationProvider.grantedAdministratorsAuthoritys) {
            if(grantedAuthoritys.contains(administratorsAuthority)) {
            	principal.setRoleAdministrators(true);
                _logger.trace("ROLE ADMINISTRATORS Authentication .");
            }
        }
        _logger.debug("Granted Authority " + grantedAuthoritys);

        principal.setGrantedAuthorityApps(grantedAuthoritys);

		return principal;
	}

	public void setLoginRepository(LoginRepository loginRepository) {
		this.loginRepository = loginRepository;
	}


}
