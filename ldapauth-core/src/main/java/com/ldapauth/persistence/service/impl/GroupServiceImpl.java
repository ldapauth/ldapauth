package com.ldapauth.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.constants.ConstsStatus;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.persistence.mapper.GroupMapper;
import com.ldapauth.persistence.service.GroupMemberService;
import com.ldapauth.persistence.service.GroupResourceService;
import com.ldapauth.persistence.service.GroupService;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.pojo.dto.ChangeStatusDTO;
import com.ldapauth.pojo.dto.GroupQueryDTO;
import com.ldapauth.pojo.entity.*;
import com.ldapauth.provision.ProvisionAction;
import com.ldapauth.provision.ProvisionService;
import com.ldapauth.provision.ProvisionTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    @Autowired
    GroupMemberService groupMemberService;

    @Autowired
    GroupResourceService groupResourceService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    SynchronizersService synchronizersService;

    @Override
    public Page<Group> fetch(GroupQueryDTO queryDTO) {
        LambdaQueryWrapper<Group> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (Objects.nonNull(queryDTO.getName())) {
            lambdaQueryWrapper.eq(Group::getName,queryDTO.getName());
        }
        if (Objects.nonNull(queryDTO.getObjectFrom())) {
            lambdaQueryWrapper.eq(Group::getObjectFrom,queryDTO.getObjectFrom());
        }
        if (Objects.nonNull(queryDTO.getStatus())) {
            lambdaQueryWrapper.eq(Group::getStatus,queryDTO.getStatus());
        }
        lambdaQueryWrapper.orderByDesc(Group::getCreateTime);
        return super.page(queryDTO.build(),lambdaQueryWrapper);
    }


    @Transactional
    @Override
    public boolean updateStatus(ChangeStatusDTO changeStatusDTO, Long updateBy, Date updateTime) {
        LambdaUpdateWrapper<Group> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Group::getId,changeStatusDTO.getId());
        lambdaUpdateWrapper.set(Group::getStatus,changeStatusDTO.getStatus());
        lambdaUpdateWrapper.set(Group::getUpdateBy,updateBy);
        lambdaUpdateWrapper.set(Group::getUpdateTime,updateTime);
        return super.update(lambdaUpdateWrapper);
    }

    @Override
    public boolean updateLdapDnAndLdapIdById(Long id, String ldapId, String ldapDn) {
        LambdaUpdateWrapper<Group> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(Group::getLdapDn,ldapDn);
        if (StringUtils.isNotEmpty(ldapId)) {
            lambdaUpdateWrapper.set(Group::getLdapId, ldapId);
        }
        lambdaUpdateWrapper.set(Group::getUpdateTime,new Date());
        lambdaUpdateWrapper.eq(Group::getId,id);
        return super.update(lambdaUpdateWrapper);
    }


    @Override
    public Group getGroupIdByLdapId(String ldapId) {
        LambdaQueryWrapper<Group> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Group::getLdapId,ldapId);
        return super.getOne(lambdaQueryWrapper);
    }

    @Override
    public boolean updateLdapDnAndByLdapId(String ldapId, String ldapDn) {
        LambdaUpdateWrapper<Group> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(Group::getLdapDn,ldapDn);
        lambdaUpdateWrapper.set(Group::getUpdateTime,new Date());
        lambdaUpdateWrapper.eq(Group::getLdapId,ldapId);
        return super.update(lambdaUpdateWrapper);
    }


    @Transactional
    @Override
    public boolean save(Group entity) {
        // 设置缺省值
        // 设置缺省值
        if (StringUtils.isEmpty(entity.getObjectFrom())) {
            Synchronizers synchronizers = synchronizersService.LdapConfig();
            if (Objects.isNull(synchronizers)) {
                entity.setObjectFrom(ConstsSynchronizers.SYSTEM);
            } else {
                if (synchronizers.getClassify().equalsIgnoreCase(ConstsSynchronizers.ACTIVEDIRECTORY) &&
                        synchronizers.getStatus() == ConstsStatus.DATA_ACTIVE) {
                    entity.setObjectFrom(ConstsSynchronizers.ACTIVEDIRECTORY);
                } else if (synchronizers.getClassify().equalsIgnoreCase(ConstsSynchronizers.OPEN_LDAP) &&
                        synchronizers.getStatus() == ConstsStatus.DATA_ACTIVE) {
                    entity.setObjectFrom(ConstsSynchronizers.OPEN_LDAP);
                } else {
                    entity.setObjectFrom(ConstsSynchronizers.SYSTEM);
                }
            }
        }
        boolean flag =  super.save(entity);
        if (flag && entity.isSync()) {
            provisionService.send(
                    ProvisionTopic.GROUP_TOPIC,
                    entity,
                    ProvisionAction.CREATE_ACTION);
        }
        return flag;
    }

    @Override
    @Transactional
    public boolean removeBatchByIds(Collection<?> list) {

        List<Group> list2 = super.listByIds((Collection<? extends Serializable>) list);

        boolean flag = super.removeBatchByIds(list);
        //删除组成员
        LambdaUpdateWrapper<GroupMember> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(GroupMember::getGroupId,list);
        groupMemberService.remove(lambdaUpdateWrapper);

        //删除组资源
        LambdaUpdateWrapper<GroupResource> groupResourceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        groupResourceLambdaUpdateWrapper.in(GroupResource::getGroupId,list);
        groupResourceService.remove(groupResourceLambdaUpdateWrapper);

        if (flag) {
            for (Group group : list2) {
                provisionService.send(
                        ProvisionTopic.GROUP_TOPIC,
                        group,
                        ProvisionAction.DELETE_ACTION);
            }
        }
        return flag;
    }

    @Transactional
    @Override
    public boolean updateById(Group entity) {
        boolean flag =  super.updateById(entity);
        if (flag && entity.isSync()) {
            provisionService.send(
                    ProvisionTopic.GROUP_TOPIC,
                    entity,
                    ProvisionAction.UPDATE_ACTION);
        }
        return flag;
    }



}
