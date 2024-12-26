package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.client.Client;
import com.ldapauth.pojo.entity.client.details.ClientCASDetails;
import com.ldapauth.pojo.entity.client.details.ClientJWTDetails;
import com.ldapauth.pojo.entity.client.details.ClientOIDCDetails;
import com.ldapauth.pojo.vo.Result;

import java.util.ArrayList;
import java.util.List;

public interface ClientService extends IService<Client> {


    /**
     * 获取用户授权的应用列表
     * @param userId
     */
    List<Client> myClient(Long userId);

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
    AppsDetails<ClientOIDCDetails> getDetailsClientId(String clientId);

    int updateStatus(ArrayList<Long> ids, int i);

    int deleteBatch(ArrayList<Long> ids);

    /**
     * 根据service获取cas对象
     * @param service
     * @param isMach
     * @return
     */
    ClientCASDetails getAppDetails(String service, boolean isMach);

    /**
     * 根据appId获取jwt对象
     * @return
     */
    ClientJWTDetails getAppsJwtDetails(Long appId);

    /**
     * 基于client查询应用
     * @param clientId
     * @param isMach
     * @return
     */
    Client getByClientId(String clientId, boolean isMach);

}
