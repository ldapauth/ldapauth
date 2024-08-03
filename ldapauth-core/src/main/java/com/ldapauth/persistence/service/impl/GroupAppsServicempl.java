package com.ldapauth.persistence.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.persistence.mapper.GroupAppsMapper;
import com.ldapauth.persistence.mapper.GroupMemberMapper;
import com.ldapauth.persistence.service.GroupAppsService;
import com.ldapauth.persistence.service.PermissionAuthenticationService;
import com.ldapauth.pojo.dto.GroupAppsQueryDTO;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.GroupApps;
import com.ldapauth.pojo.entity.GroupMember;
import com.ldapauth.pojo.vo.GroupAppsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class GroupAppsServicempl extends ServiceImpl<GroupAppsMapper, GroupApps> implements GroupAppsService {

    @Autowired
    GroupAppsMapper groupAppsMapper;
    @Autowired
    @Lazy
    PermissionAuthenticationService permissionAuthenticationService;

    @Autowired
    @Lazy
    GroupMemberMapper groupMemberMapper;

    ExecutorService executorService = Executors.newFixedThreadPool(200);

    @Override
    public Page<GroupAppsVO> fetch(GroupAppsQueryDTO queryDTO) {
        return groupAppsMapper.fetchList(queryDTO.build(),queryDTO);
    }
    @Override
    public boolean authApps(IdsDTO idsDTO, Long createdBy, Date createdTime) {
        List<GroupApps> batchData = new ArrayList<>();
        for(Long groupId : idsDTO.getIds()) {
            for(Long appId : idsDTO.getTargetIds()) {
                //判断是否已经授权
                if (!isAuth(groupId,appId)) {
                    GroupApps groupApps = new GroupApps();
                    groupApps.setAppId(appId);
                    groupApps.setGroupId(groupId);
                    groupApps.setCreateBy(createdBy);
                    groupApps.setCreateTime(createdTime);
                    groupApps.setUpdateBy(createdBy);
                    groupApps.setCreateTime(createdTime);
                    batchData.add(groupApps);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(batchData)) {
            super.saveBatch(batchData);
        }

        //异步线程刷新权限
        executorService.execute(() -> {
            permissionAuthenticationService.reloadApp();
        });
        return true;
    }


    /**
     * 判断是已经授权
     * @param groupId
     * @param appId
     * @return
     */
    private boolean isAuth(Long groupId,Long appId){
        LambdaQueryWrapper<GroupApps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupApps::getGroupId,groupId);
        queryWrapper.eq(GroupApps::getAppId,appId);
        return super.list(queryWrapper).size() > 0 ? true : false;
    }


}
