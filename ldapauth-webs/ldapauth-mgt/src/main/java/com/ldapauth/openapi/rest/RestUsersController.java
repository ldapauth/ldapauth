package com.ldapauth.openapi.rest;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.authn.realm.ldap.LdapAuthenticationRealm;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.persistence.service.OrganizationService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.pojo.vo.UserInfoPageVo;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

/**
 * @author Shi.bl
 *
 */
@Tag(
		name = "用户开放服务",
		description = "提供用户基础接口，包含新增/修改/删除/查询等基本接口"
)
@RestController
@RequestMapping(value = { "/openapi/idm/Users" })
@Slf4j
public class RestUsersController {

	@Autowired
	UserInfoService userInfoService;

	@Autowired
	OrganizationService organizationService;


	@Operation(summary = "分页查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/fetch" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<Page<UserInfoPageVo>> fetch(@ParameterObject UserInfoQueryDTO userInfoQueryDTO) {
		log.debug("-fetch:{}",userInfoQueryDTO);
		return Result.success(userInfoService.fetch(userInfoQueryDTO));
	}

	@Operation(summary = "ID查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/get/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<UserInfo> get(@Validated @NotNull(message = "ID不能为空") @PathVariable("id") Long id) {
		log.debug("-get:{}",id);
		UserInfo userInfo = userInfoService.getById(id);
		if (Objects.nonNull(userInfo)) {
			userInfo.trans();
			return Result.success(userInfo);
		}
		return Result.failed("查询ID不存在");
	}

	@Operation(summary = "新增", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value={"/add"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<String> add(@Validated(value = AddGroup.class) @RequestBody UserInfoDTO userInfoDTO) {
		log.debug("-add:{}",userInfoDTO);
		UserInfo userInfo = BeanUtil.copyProperties(userInfoDTO,UserInfo.class);
		userInfo.setCreateBy(0L);
		userInfo.setCreateTime(new Date());
		userInfo.setSync(true);
		if (userInfoService.save(userInfo)) {
			return Result.success("新增成功");
		}
		return Result.failed("新增失败");
	}

	@Operation(summary = "修改", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping(value={"/edit"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<String> edit(@Validated(value = EditGroup.class)  @RequestBody UserInfoDTO userInfoDTO) {
		log.debug("-edit:{}",userInfoDTO);
		UserInfo userInfo = BeanUtil.copyProperties(userInfoDTO,UserInfo.class,"password");
		userInfo.setUpdateBy(0L);
		userInfo.setUpdateTime(new Date());
		userInfo.setSync(true);
		if (userInfoService.updateById(userInfo)) {
			return Result.success("修改成功");
		}
		return Result.failed("修改失败");
	}

	@Operation(summary = "删除/批量删除", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@DeleteMapping("/delete")
	public Result<String> delete(@Validated @RequestBody IdsListDto idsListDto) {
		log.debug("删除-IdsListDto:{}",idsListDto);
		return userInfoService.deleteBatch(idsListDto);
	}

	@Operation(summary = "禁用/批量禁用", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping(value = { "/disable" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<String> disable(@Validated @RequestBody IdsListDto idsListDto) {
		log.debug("禁用-idsListDto:{}",idsListDto);
		return (userInfoService.disableBatch(idsListDto,0L,new Date()));
	}

	@Operation(summary = "启用/批量启用", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping(value = { "/active" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<String> active(@Validated @RequestBody IdsListDto idsListDto) {
		log.info("启用-idsListDto:{}",idsListDto);
		return (userInfoService.activeBatch(idsListDto,0L,new Date()));
	}


	@Autowired
	LdapAuthenticationRealm ldapAuthenticationRealm;

	@Operation(summary = "更新用户密码", description = "返回结果",
			security = {@SecurityRequirement(name = "Authorization")}
	)
	@ApiResponse(
			responseCode = "200",
			description = "成功"
	)
	@PutMapping("/update/password")
	public Result<String> updatePassword(@Validated @RequestBody PwdUpdateDto dto) {
		UserInfo currentUser = userInfoService.getUserByUsername(dto.getUsername());
		if (!currentUser.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
			boolean flag = ldapAuthenticationRealm.passwordMatches(currentUser,dto.getOldPassword());
			if (!flag) {
				return Result.failed("原密码错误");
			}
		}
		return userInfoService.updatePassword(dto, currentUser);
	}
}
