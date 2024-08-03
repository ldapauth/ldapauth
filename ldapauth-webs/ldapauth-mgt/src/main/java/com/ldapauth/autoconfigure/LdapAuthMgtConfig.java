package com.ldapauth.autoconfigure;

import com.ldapauth.authn.realm.ldap.LdapAuthenticationRealm;
import com.ldapauth.persistence.repository.LoginRepository;
import com.ldapauth.persistence.repository.PasswordPolicyValidator;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.persistence.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import com.ldapauth.authn.realm.jdbc.JdbcAuthenticationRealm;
import com.ldapauth.ip2region.IpRegionParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@AutoConfiguration
@Slf4j
public class LdapAuthMgtConfig {


	@Bean
	public LdapAuthenticationRealm ldapAuthenticationRealm(SynchronizersService synchronizersService){
		return new LdapAuthenticationRealm(synchronizersService);
	}

	//authenticationRealm for ldapauthMgtApplication
	@Bean
	public JdbcAuthenticationRealm authenticationRealm(
 			PasswordEncoder passwordEncoder,
	    		PasswordPolicyValidator passwordPolicyValidator,
	    		LoginRepository loginRepository,
	    		UserInfoService userInfoService,
             JdbcTemplate jdbcTemplate,
             IpRegionParser ipRegionParser,
			LdapAuthenticationRealm ldapAuthenticationRealm) {
        JdbcAuthenticationRealm authenticationRealm = new JdbcAuthenticationRealm(
        		passwordEncoder,
        		passwordPolicyValidator,
        		loginRepository,
        		userInfoService,
        		jdbcTemplate,
        		ipRegionParser,
				ldapAuthenticationRealm);
        log.debug("JdbcAuthenticationRealm inited.");
        return authenticationRealm;
    }
}
