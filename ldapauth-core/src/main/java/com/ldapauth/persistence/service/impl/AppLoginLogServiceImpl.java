package com.ldapauth.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.persistence.mapper.AppLoginLogMapper;
import com.ldapauth.persistence.service.AppLoginLogService;
import com.ldapauth.pojo.entity.AppLoginLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/23  11:24
 */

@Slf4j
@Service
public class AppLoginLogServiceImpl extends ServiceImpl<AppLoginLogMapper, AppLoginLog> implements AppLoginLogService {
}
