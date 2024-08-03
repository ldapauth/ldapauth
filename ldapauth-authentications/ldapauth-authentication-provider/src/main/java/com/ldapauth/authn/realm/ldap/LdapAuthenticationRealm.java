package com.ldapauth.authn.realm.ldap;

import com.ldapauth.authn.realm.AbstractAuthenticationRealm;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.util.LdapUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


/**
 * LDAP认证域
 *
 * @author Crystal.Sea
 *
 */
@Slf4j
public class LdapAuthenticationRealm extends AbstractAuthenticationRealm {

	SynchronizersService synchronizersService;

	/**
	 *
	 */
	public LdapAuthenticationRealm() {

	}

	public LdapAuthenticationRealm(SynchronizersService synchronizersService) {
		this.synchronizersService = synchronizersService;
	}

	@Override
	public boolean passwordMatches(UserInfo userInfo, String password) {
		boolean isAuthenticated = false;
		StandardLdapServer ldapServer = getStandardLdapServer();
		if (Objects.isNull(ldapServer)) {
			return false;
		}
		String username = userInfo.getUsername();
		log.debug("Attempting to authenticate {} at {}", username, ldapServer);
		try {
			isAuthenticated = ldapServer.authenticate(username, password);
		} catch (Exception e) {
			log.debug("Attempting Authenticated fail .");
		}
		if (isAuthenticated) {
			return true;
		}
		return false;
	}

	/**
	 * 获取LDAP认证器
	 * @return
	 */
	private StandardLdapServer getStandardLdapServer(){
		Synchronizers service = synchronizersService.LdapConfig();
		StandardLdapServer standardLdapServer = null;
		try {
			standardLdapServer = new StandardLdapServer();
			LdapUtils ldapUtils = new LdapUtils(
					service.getBaseApi(),
					service.getClientId(),
					service.getClientSecret(),
					service.getBaseDn());
			standardLdapServer.setLdapUtils(ldapUtils);
			standardLdapServer.setFilterAttribute("(uid=%s)");
		} catch (Exception e){
			log.error("LDAP配置错误");
		}
		return standardLdapServer;
	}


}
