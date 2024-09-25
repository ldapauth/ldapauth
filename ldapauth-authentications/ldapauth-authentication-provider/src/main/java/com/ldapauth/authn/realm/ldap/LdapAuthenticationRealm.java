package com.ldapauth.authn.realm.ldap;

import com.ldapauth.authn.realm.AbstractAuthenticationRealm;
import com.ldapauth.authn.realm.IAuthenticationServer;
import com.ldapauth.constants.ConstsSynchronizers;
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
		IAuthenticationServer authenticationServer = getStandardLdapServer();
		if (Objects.isNull(authenticationServer)) {
			return false;
		}
		String username = userInfo.getUsername();
		log.debug("Attempting to authenticate {} at {}", username, authenticationServer);
		try {
			isAuthenticated = authenticationServer.authenticate(username, password);
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
	private IAuthenticationServer getStandardLdapServer(){
		Synchronizers service = synchronizersService.LdapConfig();
		try {
			if (Objects.nonNull(service) && service.getClassify().equalsIgnoreCase(ConstsSynchronizers.OPEN_LDAP)) {
				StandardLdapServer standardLdapServer = new StandardLdapServer();
				LdapUtils ldapUtils = new LdapUtils(
						service.getBaseApi(),
						service.getClientId(),
						service.getClientSecret(),
						service.getBaseDn());
				standardLdapServer.setLdapUtils(ldapUtils);
				standardLdapServer.setFilterAttribute("(uid=%s)");
				return standardLdapServer;
			} else if (Objects.nonNull(service) && service.getClassify().equalsIgnoreCase(ConstsSynchronizers.ACTIVEDIRECTORY)) {
				ActiveDirectoryServer activeDirectoryServer = new ActiveDirectoryServer();
				LdapUtils ldapUtils = new LdapUtils(
						service.getBaseApi(),
						service.getClientId(),
						service.getClientSecret(),
						service.getBaseDn());
				ldapUtils.setDomain(service.getBaseDomain());
				activeDirectoryServer.setLdapUtils(ldapUtils);
				return activeDirectoryServer;
			}
		} catch (Exception e){
			log.error("LDAP配置错误");
		}
		return null;
	}


}
