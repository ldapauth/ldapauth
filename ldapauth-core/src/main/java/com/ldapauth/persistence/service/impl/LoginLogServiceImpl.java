package com.ldapauth.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.persistence.mapper.LoginLogMapper;
import com.ldapauth.persistence.service.LoginLogService;
import com.ldapauth.pojo.entity.LoginLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/23  11:19
 */
@Slf4j
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
}
