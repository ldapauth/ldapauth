package com.ldapauth.web.contorller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.authn.realm.ldap.LdapAuthenticationRealm;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.persistence.service.FileUploadService;
import com.ldapauth.persistence.service.GroupResourceService;
import com.ldapauth.persistence.service.OrganizationService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.Organization;
import com.ldapauth.pojo.entity.Resource;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.pojo.vo.UserInfoPageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author Shi.bl
 *
 */
@Tag(
		name = "用户服务",
		description = "提供用户基础接口，包含新增/修改/删除/查询等基本接口"
)
@RestController
@RequestMapping(value = { "/users" })
@Slf4j
public class UsersController {

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private FileUploadService fileUploadService;

	@Autowired
	OrganizationService organizationService;

	@Autowired
	private GroupResourceService groupResourceService;

	@Operation(summary = "获取当前用户信息", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/currentUser" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<UserInfo> currentUser() {
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		if (Objects.nonNull(currentUser)) {
			UserInfo userInfo = userInfoService.getById(currentUser.getId());
			if (Objects.nonNull(userInfo)) {
				Organization organization = organizationService.getById(userInfo.getDepartmentId());
				if (Objects.nonNull(organization)) {
					userInfo.setDepartmentName(organization.getOrgName());
				}
				return Result.success(userInfo);
			}
		}
		return Result.failed("获取失败");
	}

	@Operation(summary = "获取当前用户路由列表", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/getRouters" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<List<Resource>> getRouters() {
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		if (Objects.nonNull(currentUser)) {
			List<Resource> resourceList = groupResourceService.getResourceByMemberId(currentUser.getId());
			if (CollectionUtil.isEmpty(resourceList)) {
				resourceList = new ArrayList<>();
			}
			return Result.success(resourceList);
		}
		return Result.failed("获取失败");
	}



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
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		UserInfo userInfo = BeanUtil.copyProperties(userInfoDTO,UserInfo.class);
		userInfo.setCreateBy(currentUser.getId());
		userInfo.setCreateTime(new Date());
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
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		UserInfo userInfo = BeanUtil.copyProperties(userInfoDTO,UserInfo.class,"password");
		userInfo.setUpdateBy(currentUser.getId());
		userInfo.setUpdateTime(new Date());
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
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		log.debug("禁用-idsListDto:{}",idsListDto);
		return (userInfoService.disableBatch(idsListDto,currentUser.getId(),new Date()));
	}

	@Operation(summary = "启用/批量启用", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping(value = { "/active" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<String> active(@Validated @RequestBody IdsListDto idsListDto) {
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		log.info("启用-idsListDto:{}",idsListDto);
		return (userInfoService.activeBatch(idsListDto,currentUser.getId(),new Date()));
	}

	@Operation(
			summary = "发送手机验证码 ", description = "返回结果"
	)
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping("/sendCode")
	public Result<String> sendCode(@Validated @ParameterObject SmsCodeDto dto) {
		log.debug("-sendCode:{}",dto);
		return userInfoService.sendCode(dto);
	}

	@Operation(
			summary = "找回密码", description = "返回结果",
			parameters = {@Parameter(name = "userPassword", description = "新密码信息")}
	)
	@ApiResponse(
			responseCode = "200",
			description = "成功"
	)
	@PutMapping("/setPwd")
	public Result<String> setNewPassword(@Validated @RequestBody PwdSetDto dto) {
		UserInfo userInfo = userInfoService.getUserByUsername(dto.getMobile());
		if (Objects.nonNull(userInfo)) {
			if (!userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
				boolean flag = ldapAuthenticationRealm.passwordMatches(userInfo, dto.getPassword());
				if (!flag) {
					return Result.failed("原密码错误");
				}
			}
		}
		return userInfoService.setNewPassword(dto);
	}

	@Operation(
			summary = "修改用户头像", description = "返回结果",
			security = {@SecurityRequirement(name = "Authorization")}
	)
	@ApiResponse(
			responseCode = "200",
			description = "成功"
	)
	@PostMapping("/update/avatar")
	public Result<String> updateAvatar(@RequestPart("uploadFile") MultipartFile file) {
		UserInfo userInfo = AuthorizationUtils.getUserInfo();
		return userInfoService.updateAvatar(file, userInfo);
	}

	@Operation(summary = "更新用户资料", description = "返回结果",
	security = {@SecurityRequirement(name = "Authorization")}
	)
	@ApiResponse(
			responseCode = "200",
			description = "成功"
	)
	@PutMapping("/update/profile")
	public Result<String> updateProfile(@Validated @RequestBody ProfileUpdateDto dto) {
		UserInfo currentUser = AuthorizationUtils.getUserInfo();

		return userInfoService.updateProfile(dto, currentUser);
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
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		if (!currentUser.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
			boolean flag = ldapAuthenticationRealm.passwordMatches(currentUser, dto.getOldPassword());
			if (!flag) {
				return Result.failed("原密码错误");
			}
		}
		return userInfoService.updatePassword(dto, currentUser);
	}

	@Operation(
			summary = "管理员重置用户密码", description = "返回结果",
			parameters = {@Parameter(name = "ids", description = "用户id")},
			security = {@SecurityRequirement(name = "Authorization")}
	)
	@ApiResponse(
			responseCode = "200",
			description = "成功"
	)
	@PutMapping("/resetPwd/{id}")
	public Result<String> resetPassword(@PathVariable Long id) {
		return userInfoService.resetPassword(id);
	}
}
