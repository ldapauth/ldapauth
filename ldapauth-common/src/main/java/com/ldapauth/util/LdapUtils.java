package com.ldapauth.util;

import lombok.extern.slf4j.Slf4j;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Crystal
 *
 */
@Slf4j
public class LdapUtils {

    public static final  String propertyBaseDN = "baseDN";
    public static final  String propertyDomain = "domain";
    public static final  String propertyTrustStore = "trustStore";
    public static final  String propertyTrustStorePassword = "trustStorePassword";

    public class Product{
        public static final  String ActiveDirectory		= "ActiveDirectory";
        public static final  String OpenLDAP			= "OpenLDAP";
        public static final  String StandardLDAP		= "StandardLDAP";
    }

    protected DirContext ctx;
    protected String baseDN;
    protected String providerUrl;

    protected String domain;
    protected String principal;
    protected String credentials;
    protected String referral = "ignore";
    protected String trustStore;
    protected String trustStorePassword;
    protected boolean ssl;
    protected int searchScope;
    protected Properties props;

    /**
     *
     */
    public LdapUtils() {
        super();
        this.searchScope = SearchControls.SUBTREE_SCOPE;
    }

    public LdapUtils(String providerUrl, String principal, String credentials) {
        this.providerUrl = providerUrl;
        this.principal = principal;
        this.credentials = credentials;
        this.searchScope = SearchControls.SUBTREE_SCOPE;
    }

    public LdapUtils(String providerUrl, String principal, String credentials, String baseDN) {
        this.providerUrl = providerUrl;
        this.principal = principal;
        this.credentials = credentials;
        this.searchScope = SearchControls.SUBTREE_SCOPE;
        this.baseDN = baseDN;
    }

    public LdapUtils(DirContext dirContext) {
        this.ctx = dirContext;
    }

    public void setSearchSubTreeScope() {
        this.searchScope = SearchControls.SUBTREE_SCOPE;
    }

    public void setSearchOneLevelScope() {
        this.searchScope = SearchControls.ONELEVEL_SCOPE;
    }

    protected DirContext InitialDirContext(Properties properties) {
        if(ctx == null) {
            ctx =createDirContext(properties);
        }
        return ctx;
    }

    protected DirContext createDirContext(Properties properties) {
        DirContext ctx = null;
        try {
            ctx = new InitialDirContext(properties);
            log.info("connect to ldap " + providerUrl + " seccessful.");
        } catch (NamingException e) {
            log.error("connect to ldap " + providerUrl + " fail.");
            log.error("error",e);
        }
        return ctx;
    }

    protected void initEnvironment() {
        // LDAP
        if(props == null) {
            log.debug("PROVIDER_URL {}" , providerUrl);
            log.debug("SECURITY_PRINCIPAL {}" , principal);
            //no log credentials
            //_logger.trace("SECURITY_CREDENTIALS {}" , credentials);
            props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            props.setProperty(Context.URL_PKG_PREFIXES, "com.sun.jndi.url");
            props.setProperty(Context.REFERRAL, referral);
            props.setProperty(Context.SECURITY_AUTHENTICATION, "simple");

            props.setProperty(Context.PROVIDER_URL, providerUrl);
            props.setProperty(Context.SECURITY_PRINCIPAL, principal);
            props.setProperty(Context.SECURITY_CREDENTIALS, credentials);
            //	**解决 乱码 的关键一句
            props.put("java.naming.ldap.attributes.binary","objectGUID");

            if (ssl && providerUrl.toLowerCase().startsWith("ldaps")) {
                System.setProperty("javax.net.ssl.trustStore", trustStore);
                System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
                props.put(Context.SECURITY_PROTOCOL, "ssl");
                props.put(Context.REFERRAL, "follow");

            }
        }
    }

    // connect to ldap server
    public DirContext openConnection() {
        initEnvironment();
        return InitialDirContext(props);
    }

    // connect to ldap server
    public DirContext createConnection() {
        initEnvironment();
        return createDirContext(props);
    }

    public boolean authenticate() {
        openConnection();
        if (this.ctx != null) {
            close();
            return true;
        } else {
            return false;
        }
    }

    public void close() {
        close(this.ctx);
    }

    public void close(DirContext ctx) {
        if (null != ctx) {
            try {
                ctx.close();
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            } finally {
                ctx = null;
            }
        }
    }

    public DirContext getCtx() {
        return ctx;
    }

    public DirContext getConnection() {
        if (ctx == null) {
            openConnection();
        }

        return ctx;
    }

    /**
     * @return the baseDN
     */
    public String getBaseDN() {
        return baseDN;
    }

    /**
     * @param baseDN the baseDN to set
     */
    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    /**
     * @return the searchScope
     */
    public int getSearchScope() {
        return searchScope;
    }

    /**
     * @return the providerUrl
     */
    public String getProviderUrl() {
        return providerUrl;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    /**
     * @return the trustStore
     */
    public String getTrustStore() {
        return trustStore;
    }

    /**
     * @param trustStore the trustStore to set
     */
    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    /**
     * @return the ssl
     */
    public boolean isSsl() {
        return ssl;
    }

    /**
     * @param ssl the ssl to set
     */
    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    /**
     * @return the referral
     */
    public String getReferral() {
        return referral;
    }

    /**
     * @param referral the referral to set
     */
    public void setReferral(String referral) {
        this.referral = referral;
    }

    /**
     * @return the trustStorePassword
     */
    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    /**
     * @param trustStorePassword the trustStorePassword to set
     */
    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public static String getAttrStringValue(Attributes attrs, String elem) {
        StringBuffer  values = new StringBuffer("");
        try {
            if (attrs.get(elem) != null) {
                for (int i = 0; i < attrs.get(elem).size(); i++) {
                    if(i == 0) {
                        values.append(attrs.get(elem).get(i).toString());
                    }else {
                        values.append(" , ").append(attrs.get(elem).get(i).toString());
                    }
                }
            }
        } catch (NamingException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return values.toString();
    }

    public static String getAttrStringValue(Attribute attr) {
        StringBuffer  values = new StringBuffer("");
        try {
            if (attr != null) {
                for (int i = 0; i < attr.size(); i++) {
                    if(i == 0) {
                        values.append(attr.get(i).toString());
                    }else {
                        values.append(" , ").append(attr.get(i).toString());
                    }
                }
            }
        } catch (NamingException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return values.toString();
    }

    public static String getAttributeStringValue(String attribute ,HashMap<String,Attribute> attributeMap) throws NamingException {
        attribute= attribute.toLowerCase();
        //AD的objectGUID
        if("objectGUID".equalsIgnoreCase(attribute)) {
            try {
                byte[] GUID = (byte[])attributeMap.get(attribute).get();
                if (Objects.nonNull(GUID)) {
                    String strGUID = "";
                    strGUID = "{";
                    strGUID = strGUID + AddLeadingZero((int)GUID[3] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[2] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[1] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[0] & 0xFF);
                    strGUID = strGUID + "-";
                    strGUID = strGUID + AddLeadingZero((int)GUID[5] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[4] & 0xFF);
                    strGUID = strGUID + "-";
                    strGUID = strGUID + AddLeadingZero((int)GUID[7] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[6] & 0xFF);
                    strGUID = strGUID + "-";
                    strGUID = strGUID + AddLeadingZero((int)GUID[8] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[9] & 0xFF);
                    strGUID = strGUID + "-";
                    strGUID = strGUID + AddLeadingZero((int)GUID[10] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[11] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[12] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[13] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[14] & 0xFF);
                    strGUID = strGUID + AddLeadingZero((int)GUID[15] & 0xFF);
                    strGUID = strGUID + "}";
                    return strGUID;
                }
            }catch (NamingException e){
                log.error("error");
            }
        }
        if(null != attributeMap.get(attribute)  && null != attributeMap.get(attribute).get()) {
            return attributeMap.get(attribute).get().toString();
        }else {
            return "";
        }
    }

    public static String AddLeadingZero(int k) {
        return (k <= 0xF) ? "0" + Integer.toHexString(k) : Integer
                .toHexString(k);
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
