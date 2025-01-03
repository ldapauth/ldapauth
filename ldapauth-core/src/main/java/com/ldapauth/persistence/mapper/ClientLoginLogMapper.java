package com.ldapauth.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ldapauth.pojo.entity.client.ClientLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/23  11:21
 */
@Mapper
public interface ClientLoginLogMapper extends BaseMapper<ClientLoginLog> {
}
