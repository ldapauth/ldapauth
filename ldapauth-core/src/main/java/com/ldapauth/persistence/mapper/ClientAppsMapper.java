package com.ldapauth.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ldapauth.pojo.entity.apps.ClientApps;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClientAppsMapper extends BaseMapper<ClientApps> {


    /**
     * 获取用户授权的应用列表
     * @param userId
     */
    List<ClientApps> myApps(@Param("userId") Long userId);

}
