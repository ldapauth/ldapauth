/*
 * Copyright [2020] [MaxKey of copyright http://www.maxkey.top]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ldapauth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import java.util.Properties;

/**
 * @author Crystal
 *
 */
public class ActiveDirectoryExtUtils extends LdapUtils {
    private final static Logger _logger = LoggerFactory.getLogger(ActiveDirectoryExtUtils.class);

    protected String domain;

    String activeDirectoryDomain;

    String usersBaseDomain;
    /**
     *
     */
    public ActiveDirectoryExtUtils() {
        super();
    }

    public ActiveDirectoryExtUtils(String providerUrl, String principal, String credentials, String baseDN,
                                   String domain, String usersBaseDomain) {
        this.providerUrl = providerUrl;
        this.principal = principal;
        this.credentials = credentials;
        this.searchScope = SearchControls.SUBTREE_SCOPE;
        this.baseDN = baseDN;
        this.domain = domain.toUpperCase();
        this.usersBaseDomain = usersBaseDomain;
    }



    public ActiveDirectoryExtUtils(DirContext dirContext) {
        this.ctx = dirContext;
    }

    @Override
    protected void initEnvironment() {
    	 if(props == null) {
    		_logger.debug("PROVIDER_URL {}" , providerUrl);
            _logger.debug("SECURITY_PRINCIPAL {}" , principal);
            //no log credentials
            //_logger.trace("SECURITY_CREDENTIALS {}" , credentials);
 	        // LDAP
 	        props = new Properties();
             props.put("com.sun.jndi.ldap.read.timeout", "15000");
             props.put("com.sun.jndi.ldap.connect.timeout", "10000");
 	        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
 	        props.setProperty(Context.URL_PKG_PREFIXES, "com.sun.jndi.url");
 	        props.setProperty(Context.REFERRAL, referral);
 	        props.setProperty(Context.SECURITY_AUTHENTICATION, "simple");

 	        props.setProperty(Context.PROVIDER_URL, providerUrl);

 	        if (domain.indexOf(".") > -1) {
 	        	activeDirectoryDomain = domain.substring(0, domain.indexOf("."));
 	        }else {
 	        	activeDirectoryDomain = domain;
 	        }

 	        _logger.info("PROVIDER_DOMAIN:" + activeDirectoryDomain + " for " + domain);
 	        String activeDirectoryPrincipal = activeDirectoryDomain + "\\" + principal;
 	        _logger.debug("Active Directory SECURITY_PRINCIPAL : " + activeDirectoryPrincipal);
 	        props.setProperty(Context.SECURITY_PRINCIPAL, activeDirectoryPrincipal);
 	        props.setProperty(Context.SECURITY_CREDENTIALS, credentials);

 	        if (ssl && providerUrl.toLowerCase().startsWith("ldaps")) {
 	        	_logger.info("ldaps security protocol.");
 	            System.setProperty("javax.net.ssl.trustStore", trustStore);
 	            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
 	            props.put(Context.SECURITY_PROTOCOL, "ssl");
 	        }
 	        props.put(Context.REFERRAL, "follow");
     	 }
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain.toUpperCase();
    }

    public String getUsersBaseDomain() {
        return usersBaseDomain;
    }

    public void setUsersBaseDomain(String usersBaseDomain) {
        this.usersBaseDomain = usersBaseDomain;
    }
}
