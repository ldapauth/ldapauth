package com.ldapauth.web.contorller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.ClientService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.entity.client.Client;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/9  15:47
 */
@Tag(
        name = "应用管理",
        description = "CRUD基础功能"
)
@RestController
@RequestMapping(value = "/apps")
@Slf4j
public class ClientController {

    @Autowired
    private ClientService service;

    /**
     * 分页+
     * @return
     */
    @Operation(summary = "我的应用", description = "返回集合对象",
            security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping(value = "/myApps")
    public Result<List<Client>> myApps() {
        UserInfo currentUser = AuthorizationUtils.getUserInfo();
        return Result.success(service.myClient(currentUser.getId()));
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
    public Page list(@ParameterObject AppsQueryDTO model) {
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();

      /*  if (ObjectUtils.isNotEmpty(model.getInstId())){
            queryWrapper.eq(Apps::getInstId,model.getInstId());
        }else {
            queryWrapper.eq(Apps::getInstId, AuthorizationUtils.getCurrentUser().getInstId());
        }
*/
        if (StringUtils.isNotEmpty(model.getAppCode())) {
            queryWrapper.like(Client::getAppCode,model.getAppCode());
        }
        if (StringUtils.isNotEmpty(model.getAppName())) {
            queryWrapper.like(Client::getAppName,model.getAppName());
        }
        if (Objects.nonNull(model.getProtocol())) {
            queryWrapper.eq(Client::getProtocol,model.getProtocol());
        }
        if (Objects.nonNull(model.getStatus())) {
            queryWrapper.eq(Client::getStatus,model.getStatus());
        }
        return service.page(model.build(),queryWrapper);
    }

    @Operation(summary = "查询全部应用", description = "返回查询对象", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping(value = "/all")
    public Result<List<Client>> all() {
        return Result.success(service.list());
    }


    @Operation(
            summary = "新增OIDC", description = "返回成功信息",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @PostMapping("/oidc/create")
    public Result<String> oidcCreate(@Validated(AddGroup.class) @RequestBody AppsOidcDTO dto) {
        return service.oidcCreate(dto);
    }

    @Operation(
            summary = "修改OIDC", description = "返回成功信息",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @PutMapping("/oidc/update")
    public Result<String> oidcUpdate(@Validated(EditGroup.class) @RequestBody AppsOidcDTO dto) {
        return service.oidcUpdate(dto);
    }

    @Operation(
            summary = "新增SAML", description = "返回成功信息",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @PostMapping("/saml/create")
    public Result<String> samlCreate(@Validated(AddGroup.class) @RequestBody AppsSamlDTO dto) {
        return service.samlCreate(dto);
    }

    @Operation(
            summary = "修改SAML", description = "返回成功信息",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @PutMapping("/saml/update")
    public Result<String> jwtUpdate(@Validated(EditGroup.class) @RequestBody AppsSamlDTO dto) {
        return service.samlUpdate(dto);
    }

    @Operation(
            summary = "新增JWT", description = "返回成功信息",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @PostMapping("/jwt/create")
    public Result<String> jwtCreate(@Validated(AddGroup.class) @RequestBody AppsJwtDTO dto) {
        return service.jwtCreate(dto);
    }

    @Operation(
            summary = "修改JWT", description = "返回成功信息",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @PutMapping("/jwt/update")
    public Result<String> jwtUpdate(@Validated(EditGroup.class) @RequestBody AppsJwtDTO dto) {
        return service.jwtUpdate(dto);
    }

    @Operation(
            summary = "新增CAS", description = "返回成功信息",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @PostMapping("/cas/create")
    public Result<String> casCreate(@Validated(AddGroup.class) @RequestBody AppsCasDTO dto) {
        return service.casCreate(dto);
    }

    @Operation(
            summary = "修改CAS", description = "返回成功信息",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @PutMapping("/cas/update")
    public Result<String> casUpdate(@Validated(EditGroup.class) @RequestBody AppsCasDTO dto) {
        return service.casUpdate(dto);
    }

    @Operation(summary = "ID查询IDP扩展信息", description = "返回对象", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("/appdetails/{id}")
    public Result<AppsDetails> get(@NotNull(message = "应用ID不能为空") @PathVariable Long id) {
        return service.getDetails(id);
    }

    @Operation(
            summary = "启用", description = "返回结果",
            parameters = {@Parameter(name = "ids", description = "编码")},
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @PutMapping("/active/{ids}")
    public Result<String> active(@PathVariable("ids") ArrayList<Long> ids) {
        try {
            //InstVO instVO = new InstVO(ids, AuthorizationUtils.getCurrentUser().getInstId());
            //instVO.setStatus(0);
            int count = service.updateStatus(ids,0);
            return Result.success("启用成功");
        } catch (Exception e) {
            return Result.failed("启用失败");
        }
    }

    @PutMapping("/disable/{ids}")
    public Result<String> disable(@PathVariable("ids") ArrayList<Long> ids) {
        try {
            //InstVO instVO = new InstVO(ids,AuthorizationUtils.getCurrentUser().getInstId());
            //instVO.setStatus(1);
            int count = service.updateStatus(ids,1);
            return Result.success("禁用成功");
        } catch (Exception e) {
            return Result.failed("禁用失败");
        }
    }

    @Operation(
            summary = "删除", description = "返回结果",
            parameters = {@Parameter(name = "ids", description = "ID集合")},
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @DeleteMapping("/delete/{ids}")
    public Result<String> delete(@PathVariable("ids") ArrayList<Long> ids) {
        try {
            service.deleteBatch(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("error",e);
            return Result.failed("删除失败");
        }
    }



}
