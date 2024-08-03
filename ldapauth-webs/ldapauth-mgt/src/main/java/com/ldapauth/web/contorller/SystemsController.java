package com.ldapauth.web.contorller;

import cn.hutool.core.bean.BeanUtil;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.SystemsService;
import com.ldapauth.pojo.dto.SystemsDTO;
import com.ldapauth.pojo.entity.PolicyLogin;
import com.ldapauth.pojo.entity.PolicyPassword;
import com.ldapauth.pojo.entity.Systems;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;

/**
 * @description:
 * @author: orangeBabu
 * @time: 12/7/2024 PM2:24
 */

@Tag(
    name = "系统配置信息",
    description = "提供系统配置，系统标题、版权、LOGO修改等 功能"
)
@RestController
@RequestMapping(value = "systems")
@Slf4j
public class SystemsController {

    @Autowired
    SystemsService systemsService;

    @Operation(summary = "", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping(value = { "/get" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Result<Systems> get() {
        log.debug("-login");
        Systems systems = systemsService.get();
        if (Objects.nonNull(systems)) {
            return Result.success(systems);
        }
        return Result.failed("查询不存在");
    }

    @Operation(summary = "保存配置", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping(value={"/save"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Result<String> save(@Validated(value = EditGroup.class) @RequestBody SystemsDTO systemsDTO) {
        log.debug("-save:{}",systemsDTO);
        UserInfo currentUser = AuthorizationUtils.getUserInfo();
        Systems systems = BeanUtil.copyProperties(systemsDTO,Systems.class);
        systems.setUpdateBy(currentUser.getId());
        systems.setUpdateTime(new Date());
        if (systemsService.updateById(systems)) {
            return Result.success("保存成功");
        }
        return Result.failed("保存失败");
    }
}
