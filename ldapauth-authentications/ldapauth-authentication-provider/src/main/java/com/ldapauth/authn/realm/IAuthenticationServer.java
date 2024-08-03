package com.ldapauth.authn.realm;

/**
 * IAuthenticationServer .认证服务器
 *
 * @author Crystal.Sea
 *
 */
public interface IAuthenticationServer {

    public boolean authenticate(String username, String password);

    public boolean isMapping();
}
