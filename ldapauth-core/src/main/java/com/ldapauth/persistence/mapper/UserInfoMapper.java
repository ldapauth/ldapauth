package com.ldapauth.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.pojo.dto.UserInfoQueryDTO;
import com.ldapauth.pojo.entity.Group;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.UserInfoPageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 获取用户分组集合
     * @param userId
     * @return
     */
    List<Group> selectUserGroup(@Param("userId") Long userId);

    Page<UserInfoPageVo> selectUserPage(Page page, @Param("mapParams") UserInfoQueryDTO userInfoQueryDTO);

    List<UserInfo> checkInfoByMobile(@Param("mobile") String mobile);

    List<UserInfo> checkInfoByEmail(@Param("email") String email);

    List<UserInfo> checkInfoByUsername(@Param("username") String username);
}
