package com.ldapauth.persistence.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.persistence.mapper.GroupMemberMapper;
import com.ldapauth.persistence.mapper.GroupResourceMapper;
import com.ldapauth.persistence.service.GroupResourceService;
import com.ldapauth.persistence.service.PermissionAuthenticationService;
import com.ldapauth.pojo.dto.GroupMemberQueryDTO;
import com.ldapauth.pojo.dto.GroupResourceQueryDTO;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.GroupMember;
import com.ldapauth.pojo.entity.GroupResource;
import com.ldapauth.pojo.entity.Resource;
import com.ldapauth.pojo.vo.IdsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupResourceServicempl extends ServiceImpl<GroupResourceMapper, GroupResource> implements GroupResourceService {

    @Autowired
    GroupResourceMapper groupResourceMapper;

    @Autowired

    GroupMemberMapper groupMemberMapper;

    @Autowired
    CacheService cacheService;

    @Autowired
    @Lazy
    PermissionAuthenticationService permissionAuthenticationService;

    ExecutorService executorService = Executors.newFixedThreadPool(200);


    @Override
    public Page<Resource> fetch(GroupResourceQueryDTO queryDTO) {
        return groupResourceMapper.fetch(queryDTO.build(),queryDTO);
    }

    @Override
    public IdsVO getAuthResourceIds(Long groupId) {
        LambdaQueryWrapper<GroupResource> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupResource::getGroupId,groupId);
        List<GroupResource> list = super.list(lambdaQueryWrapper);
        IdsVO vo = new IdsVO();
        if (CollectionUtil.isEmpty(list)) {
            vo.setIds(new ArrayList<>());
        } else {
            vo.setIds(list.stream().map(GroupResource::getResourceId).collect(Collectors.toList()));
        }
        return vo;
    }


    @Override
    public boolean authResource(IdsDTO idsDTO, Long createdBy, Date createdTime) {
        if (CollectionUtil.isEmpty(idsDTO.getIds())) {
            log.debug("ids集合不能为空");
            return false;
        }
        for (Long groupId : idsDTO.getIds()) {
            //删除组资源重新授权
            LambdaUpdateWrapper<GroupResource> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(GroupResource::getGroupId,groupId);
            groupResourceMapper.delete(lambdaUpdateWrapper);

            for (Long targetId : idsDTO.getTargetIds()) {
                GroupResource member = new GroupResource();
                member.setGroupId(groupId);
                member.setResourceId(targetId);
                member.setCreateBy(createdBy);
                member.setCreateTime(createdTime);
                member.setUpdateBy(createdBy);
                member.setUpdateTime(createdTime);
                groupResourceMapper.insert(member);
            }
        }
        //异步线程刷新权限
        executorService.execute(() -> {
            permissionAuthenticationService.reloadApi();
        });
        return true;
    }


    @Override
    public List<Resource> getResourceByMemberId(Long memberId) {
        return groupResourceMapper.selectResourceByMemberId(memberId);
    }

    /**
     * 判刑分组资源是否存在
     * @param groupId
     * @param resourceId
     * @return
     */
    private boolean isGroupResourceExist(long groupId,Long resourceId){
        LambdaUpdateWrapper<GroupResource> query = new LambdaUpdateWrapper<>();
        query.eq(GroupResource::getGroupId,groupId);
        query.eq(GroupResource::getResourceId,resourceId);
        List<GroupResource> list = groupResourceMapper.selectList(query);
        if (CollectionUtil.isNotEmpty(list) && list.size() >0) {
            return true;
        }
        return false;
    }
}
