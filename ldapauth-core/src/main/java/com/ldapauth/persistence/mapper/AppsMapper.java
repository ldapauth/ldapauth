package com.ldapauth.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ldapauth.pojo.entity.apps.Apps;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppsMapper extends BaseMapper<Apps> {


    /**
     * 获取用户授权的应用列表
     * @param userId
     */
    List<Apps> myApps(@Param("userId") Long userId);

}
