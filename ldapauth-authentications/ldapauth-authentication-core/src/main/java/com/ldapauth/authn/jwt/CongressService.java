


package com.ldapauth.authn.jwt;

/**
 * congress服务接口
 *
 * @author Crystal.Sea
 *
 */
public interface CongressService {
    void store(String congress, AuthJwt authJwt);
    AuthJwt consume(String congress);
    AuthJwt remove(String congress);
    AuthJwt get(String congress);

}
