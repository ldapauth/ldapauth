package com.ldapauth.persistence.service;



/**
 * @author Shi.bl
 *
 */
public interface PermissionAuthenticationService {



    /**
     * 鉴权api
     * @param userId
     * @param path
     * @return
     */
    boolean authApi(Long userId,String path);


    /**
     * 鉴权应用
     * @param userId
     * @param appId
     * @return
     */
    boolean authApp(Long userId,Long appId);


    /**
     * 重新加载资源
     * @return
     */
    boolean reloadApi();

    /**
     * 重新加载应用
     * @return
     */
    boolean reloadApp();
}
