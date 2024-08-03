package com.ldapauth.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.pojo.dto.GroupMemberQueryDTO;
import com.ldapauth.pojo.entity.GroupMember;
import com.ldapauth.pojo.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GroupMemberMapper extends BaseMapper<GroupMember> {
    /**
     * 查询授权的组成员
     * @param page
     * @param query
     * @return
     */
    Page<UserInfo> selectAuthList(Page page,@Param("query") GroupMemberQueryDTO query);

    /**
     * 查询未授权的组成员
     * @param page
     * @param query
     * @return
     */
    Page<UserInfo> selectNotAuthList(Page page,@Param("query") GroupMemberQueryDTO query);

}
