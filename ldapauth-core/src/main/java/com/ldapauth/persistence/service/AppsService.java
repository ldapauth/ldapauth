package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.apps.Apps;
import com.ldapauth.pojo.entity.apps.details.AppsCasDetails;
import com.ldapauth.pojo.entity.apps.details.AppsJwtDetails;
import com.ldapauth.pojo.entity.apps.details.AppsOidcDetails;
import com.ldapauth.pojo.vo.Result;

import java.util.ArrayList;
import java.util.List;

public interface AppsService extends IService<Apps> {


    /**
     * 获取用户授权的应用列表
     * @param userId
     */
    List<Apps> myApps(Long userId);

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
    AppsDetails<AppsOidcDetails> getDetailsClientId(String clientId);

    int updateStatus(ArrayList<Long> ids, int i);

    int deleteBatch(ArrayList<Long> ids);

    /**
     * 根据service获取cas对象
     * @param service
     * @param isMach
     * @return
     */
    AppsCasDetails getAppDetails(String service, boolean isMach);

    /**
     * 根据appId获取jwt对象
     * @return
     */
    AppsJwtDetails getAppsJwtDetails(Long appId);

    /**
     * 基于client查询应用
     * @param clientId
     * @param isMach
     * @return
     */
    Apps getByClientId(String clientId, boolean isMach);

}
