package com.ldapauth.web.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lark.oapi.core.annotation.Body;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.SmsProviderService;
import com.ldapauth.pojo.dto.CreateSmsProviderDto;
import com.ldapauth.pojo.dto.IdsListDto;
import com.ldapauth.pojo.dto.SmsProviderQueryDto;
import com.ldapauth.pojo.entity.SmsProvider;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: orangeBabu
 * @time: 15/7/2024 AM9:46
 */

@Tag(
        name = "短信提供商配置",
        description = "CRUD基础功能"
)
@RestController
@RequestMapping("/provider")
@Slf4j
public class SmsSendersController {

    @Autowired
    SmsProviderService smsProviderService;

    @Operation(
            summary = "新增配置", description = "返回结果",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "成功"
    )
    @PostMapping("/add")
    public Result<String> create(@RequestBody @Validated CreateSmsProviderDto dto) {
        log.debug("-add:{}",dto);
        UserInfo currentUser = AuthorizationUtils.getUserInfo();
        SmsProvider smsProvider = new SmsProvider();
        BeanUtils.copyProperties(dto, smsProvider);
        smsProvider.setCreateBy(currentUser.getId());
        smsProvider.setCreateTime(new Date());
        return smsProviderService.add(smsProvider);
    }

    /**
     * 修改
     * @return
     */
    @Operation(
            summary = "修改配置", description = "返回结果",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "成功"
    )
    @PutMapping("/edit")
    public Result<String> update(@RequestBody @Validated(value = EditGroup.class) CreateSmsProviderDto dto) {
        log.debug("-update:{}",dto);
        UserInfo currentUser = AuthorizationUtils.getUserInfo();
        SmsProvider smsProvider = new SmsProvider();
        BeanUtils.copyProperties(dto, smsProvider);
        smsProvider.setUpdateBy(currentUser.getId());
        smsProvider.setUpdateTime(new Date());
        return smsProviderService.edit(smsProvider);
    }



    /**
     * 分页
     * @return
     */
    @Operation(
            summary = "分页查询", description = "返回查询对象",
            security = {@SecurityRequirement(name = "Authorization")}
    )

    @ApiResponse(
            responseCode = "200",
            description = "成功"
    )
    @GetMapping(value = "/list")
    public Result<Page<SmsProvider>> list(@ParameterObject SmsProviderQueryDto dto) {
        LambdaQueryWrapper<SmsProvider> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(dto.getProvider())){
            queryWrapper.like(SmsProvider::getProvider, dto.getProvider());
        }
        queryWrapper.orderByDesc(SmsProvider::getCreateTime);
        return Result.success(smsProviderService.page(dto.build(),queryWrapper));
    }

    @Operation(
            summary = "ID查询", description = "返回查询对象",
            parameters = {@Parameter(name = "id", description = "配置ID")},
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "成功"
    )
    @GetMapping(value = "/get/{id}")
    public Result<SmsProvider> detail(@PathVariable Long id) {
        log.info("request time:{} , param name:{}", LocalTime.now(),id);
        return smsProviderService.get(id);
    }

    @Operation(
            summary = "删除配置", description = "返回对象",
            parameters = {@Parameter(name = "ids", description = "配置ID")},
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "成功"
    )
    @DeleteMapping(value = "/delete")
    public Result delete(@RequestBody @Validated IdsListDto dto) {
        log.info("request time:{} , param name:{}",LocalTime.now(),dto);
        return smsProviderService.delete(dto);
    }

    @Operation(
            summary = "禁用", description = "返回结果",
            parameters = {@Parameter(name = "ids", description = "配置ID")},
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "成功"
    )
    @PutMapping("/disable/{id}")
    public Result<String> disable(@PathVariable Long id) {
        log.info("id:{}",id);
        return smsProviderService.disable(id);
    }

    @Operation(
            summary = "启用", description = "返回结果",
            parameters = {@Parameter(name = "ids", description = "配置ID")},
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "成功"
    )
    @PutMapping("/active/{id}")
    public Result<String> active(@PathVariable Long id) {
        log.info("id:{}",id);
        return smsProviderService.active(id);
    }
}
