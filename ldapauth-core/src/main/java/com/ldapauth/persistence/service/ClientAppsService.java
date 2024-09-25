package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.apps.ClientApps;
import com.ldapauth.pojo.entity.apps.details.ClientAppsCASDetails;
import com.ldapauth.pojo.entity.apps.details.ClientAppsJWTDetails;
import com.ldapauth.pojo.entity.apps.details.ClientAppsOIDCDetails;
import com.ldapauth.pojo.vo.Result;

import java.util.ArrayList;
import java.util.List;

public interface ClientAppsService extends IService<ClientApps> {


    /**
     * 获取用户授权的应用列表
     * @param userId
     */
    List<ClientApps> myApps(Long userId);

    Result<String> oidcCreate(AppsOidcDTO dto);
    Result<String> oidcUpdate(AppsOidcDTO dto);

    Result<String> samlCreate(AppsSamlDTO dto);
    Result<String> samlUpdate(AppsSamlDTO dto);

    Result<String> jwtCreate(AppsJwtDTO dto);
    Result<String> jwtUpdate(AppsJwtDTO dto);

    Result<String> casCreate(AppsCasDTO dto);
    Result<String> casUpdate(AppsCasDTO dto);

    Result<AppsDetails> getDetails(Long id);

    /**
     * 基于client查询
     * @param clientId
     * @return
     */
    AppsDetails<ClientAppsOIDCDetails> getDetailsClientId(String clientId);

    int updateStatus(ArrayList<Long> ids, int i);

    int deleteBatch(ArrayList<Long> ids);

    /**
     * 根据service获取cas对象
     * @param service
     * @param isMach
     * @return
     */
    ClientAppsCASDetails getAppDetails(String service, boolean isMach);

    /**
     * 根据appId获取jwt对象
     * @return
     */
    ClientAppsJWTDetails getAppsJwtDetails(Long appId);

    /**
     * 基于client查询应用
     * @param clientId
     * @param isMach
     * @return
     */
    ClientApps getByClientId(String clientId, boolean isMach);

}
