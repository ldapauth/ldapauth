package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.GroupAppsQueryDTO;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.GroupApps;
import com.ldapauth.pojo.vo.GroupAppsVO;

import java.util.Date;

public interface GroupAppsService extends IService<GroupApps> {


    /**
     * 查询已授权列表
     * @param queryDTO
     * @return
     */
    Page<GroupAppsVO> fetch(GroupAppsQueryDTO queryDTO);

    /**
     * 授权分组和应用
     * @param idsDTO
     * @param createdBy 操作人
     * @param createdTime 操作时间
     * @return
     */
    boolean authApps(IdsDTO idsDTO, Long createdBy, Date createdTime);
}
