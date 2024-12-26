package com.ldapauth.web.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.ClientLoginLogService;
import com.ldapauth.pojo.dto.AppLoginLogQueryDTO;
import com.ldapauth.pojo.entity.client.ClientLoginLog;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.util.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/23  11:46
 */
@Tag(
        name = "应用访问日志",
        description = "CRUD基础功能"
)
@RestController
@RequestMapping(value = "/audit/applogin")
@Slf4j
public class ClientLoginLogController {
    @Autowired
    private ClientLoginLogService service;

    @Operation(
            summary = "查询应用访问日志", description = "查询应用访问日志",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @GetMapping("/list")
    public Page list(AppLoginLogQueryDTO model) {
        SignPrincipal signPrincipal = AuthorizationUtils.getPrincipal();
        UserInfo currentUser = signPrincipal.getUserInfo();
        LambdaQueryWrapper<ClientLoginLog> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(model.getDisplayName())) {
            queryWrapper.like(ClientLoginLog::getDisplayName,model.getDisplayName());
        }
        if (StringUtils.isNotEmpty(model.getAppName())) {
            queryWrapper.like(ClientLoginLog::getAppName,model.getAppName());
        }
        //如果是超级管理员，则设置全部查询，否则只能查询自己
        if (!signPrincipal.isRoleAdministrators()) {
            queryWrapper.eq(ClientLoginLog::getUserId,currentUser.getId());
        }
        if (model.getStartDate() != null && model.getEndDate() != null) {
            queryWrapper.between(ClientLoginLog::getCreateTime, model.getStartDate(), model.getEndDate());
        } else {
            queryWrapper.between(ClientLoginLog::getCreateTime,
                    DateUtils.format(new Date(new Date().getTime() - 1000 * 60 * 60 * 24), DateUtils.FORMAT_DATE_YYYY_MM_DD_HH_MM_SS),
                    DateUtils.format(new Date(), DateUtils.FORMAT_DATE_YYYY_MM_DD_HH_MM_SS));
        }
        return service.page(model.build(),queryWrapper);
    }
}
