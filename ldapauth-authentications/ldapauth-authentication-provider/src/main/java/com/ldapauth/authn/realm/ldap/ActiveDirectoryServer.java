package com.ldapauth.authn.realm.ldap;


import com.ldapauth.authn.realm.IAuthenticationServer;
import com.ldapauth.util.LdapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Crystal.Sea
 *
 */
public final class ActiveDirectoryServer implements IAuthenticationServer {
	private final static Logger _logger = LoggerFactory.getLogger(ActiveDirectoryServer.class);


	String filter;

	boolean mapping;

	LdapUtils ldapUtils;

	/* (non-Javadoc)
	 * @see com.connsec.web.authentication.realm.IAuthenticationServer#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean authenticate(String username, String password) {
		username = username +"@"+ ldapUtils.getDomain();
		LdapUtils ldapPassWordValid =
    		        new LdapUtils(
							ldapUtils.getProviderUrl(),
    		                username,
    		                password
    		         );
		ldapPassWordValid.openConnection();
		if(ldapPassWordValid.getCtx() != null){
			_logger.debug("Active Directory user " + username + "  is validate .");
			ldapPassWordValid.close();
			return true;
		}
		ldapPassWordValid.close();
		return false;
	}



	public LdapUtils getLdapUtils() {
		return ldapUtils;
	}

	public void setLdapUtils(LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public boolean isMapping() {
		return mapping;
	}

	public void setMapping(boolean mapping) {
		this.mapping = mapping;
	}
}
