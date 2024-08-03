package com.ldapauth.web.contorller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.constants.ConstsSystem;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.persistence.service.GroupService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.Group;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.entity.apps.Apps;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Shi.bl
 *
 */
@Tag(
		name = "分组服务",
		description = "提供分组基础接口，包含新增/修改/删除/查询等基本接口"
)
@RestController
@RequestMapping(value = { "/groups" })
@Slf4j
public class GroupsController {

	@Autowired
	private GroupService groupService;

	@Operation(summary = "分页查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/fetch" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<Page<Group>> fetch(@ParameterObject GroupQueryDTO queryDTO) {
		log.debug("-fetch:{}",queryDTO);
		return Result.success(groupService.fetch(queryDTO));
	}

	@Operation(summary = "查询全部分组", description = "返回查询对象", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = "/all")
	public Result<List<Group>> all() {
		return Result.success(groupService.list());
	}

	@Operation(summary = "ID查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/get/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<Group> get(@Validated @NotNull(message = "ID不能为空") @PathVariable("id") Long id) {
		log.debug("-get:{}",id);
		Group group = groupService.getById(id);
		if (Objects.nonNull(group)) {
			return Result.success(group);
		}
		return Result.failed("查询ID不存在");
	}

	@Operation(summary = "新增", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value={"/add"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> add(@Validated(value = AddGroup.class) @RequestBody GroupDTO groupDTO) {
		log.debug("-add:{}",groupDTO);
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		Group group = BeanUtil.copyProperties(groupDTO,Group.class);
		group.setCreateBy(currentUser.getId());
		group.setCreateTime(new Date());
		if (groupService.save(group)) {
			return Result.success("新增成功");
		}
		return Result.failed("新增失败");
	}

	@Operation(summary = "修改", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping(value={"/edit"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> edit(@Validated(value = EditGroup.class)  @RequestBody GroupDTO groupDTO) {
		log.debug("-edit:{}",groupDTO);
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		Group group = BeanUtil.copyProperties(groupDTO,Group.class);
		group.setUpdateBy(currentUser.getId());
		group.setUpdateTime(new Date());
		if (groupService.updateById(group)) {
			return Result.success("修改成功");
		}
		return Result.failed("修改失败");
	}

	@Operation(summary = "删除/批量删除", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@DeleteMapping(value={"/delete/{ids}"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> delete(@NotEmpty(message = "ID集合不能为空") @PathVariable Long[] ids) {
		log.debug("-delete:{}",ids);
		for(Long id : ids) {
			//判断是否管理员组
			if (id == ConstsSystem.SYS_ADMINISTRATOR_GROUP_ID) {
				throw  new BusinessException(HttpStatus.BAD_REQUEST.value(),"系统管理员组不能删除");
			}
		}
		if (groupService.removeBatchByIds(Arrays.asList(ids))) {
			return Result.success("删除成功");
		}
		return Result.failed("删除失败");
	}

	@Operation(summary = "修改状态", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping(value = { "/updateStatus" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> updateStatus(@Validated(value = EditGroup.class)
										   @RequestBody ChangeStatusDTO changeStatusDTO
										  ) {
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		log.debug("-updateStatus:{}",changeStatusDTO);
		if (groupService.updateStatus(changeStatusDTO,currentUser.getId(),new Date())) {
			return Result.success("修改状态成功");
		}
		return Result.failed("修改状态失败");
	}


}
