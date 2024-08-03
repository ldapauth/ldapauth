package com.ldapauth.persistence.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.persistence.mapper.GroupMemberMapper;
import com.ldapauth.persistence.service.GroupMemberService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.dto.GroupMemberQueryDTO;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.GroupMember;
import com.ldapauth.pojo.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupMemberServicempl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberService {
    @Autowired
    GroupMemberMapper groupMemberMapper;

    @Autowired
    UserInfoService userInfoService;

    @Override
    public Page<UserInfo> fetchAuthList(GroupMemberQueryDTO queryDTO) {
        return groupMemberMapper.selectAuthList(queryDTO.build(),queryDTO);
    }

    @Override
    public Page<UserInfo> fetchNotAuthList(GroupMemberQueryDTO queryDTO) {
        return groupMemberMapper.selectNotAuthList(queryDTO.build(),queryDTO);
    }

    @Override
    @Transactional
    public boolean addMember(IdsDTO idsDTO, Long createdBy, Date createdTime) {
        if (CollectionUtil.isEmpty(idsDTO.getTargetIds())) {
            log.debug("itargetIds集合不能为空");
            return false;
        }
        if (CollectionUtil.isEmpty(idsDTO.getIds())) {
            log.debug("ids集合不能为空");
            return false;
        }
        for (Long groupId : idsDTO.getIds()) {
            for (Long targetId : idsDTO.getTargetIds()) {
                //检查分组成功是否存在
                if (!isGroupMemberExist(groupId,targetId)) {
                    GroupMember member = new GroupMember();
                    member.setGroupId(groupId);
                    member.setMemberId(targetId);
                    member.setCreateBy(createdBy);
                    member.setCreateTime(createdTime);
                    member.setUpdateBy(createdBy);
                    member.setUpdateTime(createdTime);
                    super.save(member);
                }
            }
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeMember(IdsDTO idsDTO, Long createdBy, Date createdTime) {
        if (CollectionUtil.isEmpty(idsDTO.getTargetIds())) {
            log.debug("itargetIds集合不能为空");
            return false;
        }
        if (CollectionUtil.isEmpty(idsDTO.getIds())) {
            log.debug("ids集合不能为空");
            return false;
        }
        for (Long groupId : idsDTO.getIds()) {
            for (Long targetId : idsDTO.getTargetIds()) {
                LambdaUpdateWrapper<GroupMember> deleteQuery = new LambdaUpdateWrapper<>();
                deleteQuery.eq(GroupMember::getGroupId,groupId);
                deleteQuery.eq(GroupMember::getMemberId,targetId);
                super.remove(deleteQuery);
            }
        }
        return true;
    }

    @Override
    public List<UserInfo> getUserListByGroupId(Long groupId) {
        LambdaQueryWrapper<GroupMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupMember::getGroupId,groupId);
        List<GroupMember> members = super.list(queryWrapper);
        if (CollectionUtil.isNotEmpty(members)) {
            List<Long> userIds = members.stream().map(GroupMember::getMemberId).collect(Collectors.toList());
            return userInfoService.listByIds(userIds);
        }
        return new ArrayList<>();
    }

    /**
     * 判刑分组成员是否存在
     * @param groupId
     * @param memberId
     * @return
     */
    private boolean isGroupMemberExist(long groupId,Long memberId){
        LambdaUpdateWrapper<GroupMember> query = new LambdaUpdateWrapper<>();
        query.eq(GroupMember::getGroupId,groupId);
        query.eq(GroupMember::getMemberId,memberId);
        List<GroupMember> list = groupMemberMapper.selectList(query);
        if (CollectionUtil.isNotEmpty(list) && list.size() >0) {
            return true;
        }
        return false;
    }
}
