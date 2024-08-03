package com.ldapauth.web.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.LoginLogService;
import com.ldapauth.pojo.dto.LoginLogQueryDTO;
import com.ldapauth.pojo.entity.LoginLog;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.util.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/23  11:13
 */
@Tag(
        name = "登录日志",
        description = "CRUD基础功能"
)
@RestController
@RequestMapping(value = "/audit/login")
@Slf4j
public class LoginLogController {

    @Autowired
    private LoginLogService service;

    @Operation(
            summary = "查询登录日志", description = "查询登录日志",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @GetMapping("/list")
    public Page list(LoginLogQueryDTO model) {
        SignPrincipal signPrincipal = AuthorizationUtils.getPrincipal();
        UserInfo currentUser = signPrincipal.getUserInfo();
        LambdaQueryWrapper<LoginLog> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(model.getDisplayName())) {
            queryWrapper.eq(LoginLog::getDisplayName,model.getDisplayName());
        }
        if (StringUtils.isNotEmpty(model.getLoginType())) {
            queryWrapper.like(LoginLog::getLoginType,model.getLoginType());
        }
        if (StringUtils.isNotEmpty(model.getProvider())) {
            queryWrapper.like(LoginLog::getProvider,model.getProvider());
        }
        if (Objects.nonNull(model.getIpAddr())) {
            queryWrapper.like(LoginLog::getIpAddr,model.getIpAddr());
        }
        if (Objects.nonNull(model.getStatus())) {
            queryWrapper.eq(LoginLog::getStatus,model.getStatus());
        }
        //如果是超级管理员，则设置全部查询，否则只能查询自己
        if (!signPrincipal.isRoleAdministrators()) {
            queryWrapper.eq(LoginLog::getUserId,currentUser.getId());
        }
        if (model.getStartDate() != null && model.getEndDate() != null) {
            queryWrapper.between(LoginLog::getCreateTime, model.getStartDate(), model.getEndDate());
        } else {
            queryWrapper.between(LoginLog::getCreateTime,
                    DateUtils.format(new Date(new Date().getTime() - 1000 * 60 * 60 * 24), DateUtils.FORMAT_DATE_YYYY_MM_DD_HH_MM_SS),
                    DateUtils.format(new Date(), DateUtils.FORMAT_DATE_YYYY_MM_DD_HH_MM_SS));
        }
        return service.page(model.build(),queryWrapper);
    }
}
