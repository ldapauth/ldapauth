package com.ldapauth.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ldapauth.pojo.entity.client.Client;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClientMapper extends BaseMapper<Client> {


    /**
     * 获取用户授权的应用列表
     * @param userId
     */
    List<Client> myClient(@Param("userId") Long userId);

}
