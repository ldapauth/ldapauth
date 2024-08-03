package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.GroupMemberQueryDTO;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.GroupMember;
import com.ldapauth.pojo.entity.UserInfo;

import java.util.Date;
import java.util.List;


public interface GroupMemberService extends IService<GroupMember> {


    /**
     * 分页查询方法,查询授权的组成员
     * @param queryDTO 分页查询参数
     * @return
     */
    Page<UserInfo> fetchAuthList(GroupMemberQueryDTO queryDTO);

    /**
     * 分页查询方法,查询未授权的组成员
     * @param queryDTO 分页查询参数
     * @return
     */
    Page<UserInfo> fetchNotAuthList(GroupMemberQueryDTO queryDTO);

    /**
     * 添加组成员
     * @param idsDTO
     * @param createdBy 操作人
     * @param createdTime 操作时间
     * @return
     */
    boolean addMember(IdsDTO idsDTO, Long createdBy, Date createdTime);

    /**
     * 删除组成员
     * @param idsDTO
     * @param createdBy 操作人
     * @param createdTime 操作时间
     * @return
     */
    boolean removeMember(IdsDTO idsDTO, Long createdBy, Date createdTime);


    /**
     * 根据组ID查询用户列表
     * @param groupId 组ID
     * @return
     */
    List<UserInfo> getUserListByGroupId(Long groupId);


}
