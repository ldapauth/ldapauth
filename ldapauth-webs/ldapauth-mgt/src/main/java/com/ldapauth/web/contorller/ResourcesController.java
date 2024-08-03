package com.ldapauth.web.contorller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.ResourceService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.Resource;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import com.sun.tools.javac.util.List;
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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * @author Shi.bl
 *
 */
@Tag(
		name = "资源服务",
		description = "提供资源基础接口，包含新增/修改/删除/查询等基本接口"
)
@RestController
@RequestMapping(value = { "/resources" })
@Slf4j
public class ResourcesController {

	@Autowired
	private ResourceService resourceService;

	@Operation(summary = "分页查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/fetch" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<java.util.List<Tree<String>>> fetch(@ParameterObject ResourceQueryDTO queryDTO) {
		log.debug("-fetch:{}");
		return Result.success(resourceService.fetch(queryDTO));
	}

	@Operation(summary = "获取系统树结构", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/systemTree" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<java.util.List<Tree<String>>> systemTree() {
		log.debug("-tree:{}");
		return Result.success(resourceService.systemTree());
	}

	@Operation(summary = "ID查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/get/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<Resource> get(@Validated @NotNull(message = "ID不能为空") @PathVariable("id") Long id) {
		log.debug("-get:{}",id);
		Resource resource = resourceService.getById(id);
		if (Objects.nonNull(resource)) {
			return Result.success(resource);
		}
		return Result.failed("查询ID不存在");
	}

	@Operation(summary = "新增", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value={"/add"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> add(@Validated(value = AddGroup.class) @RequestBody ResourceDTO resourceDTO) {
		log.debug("-add:{}",resourceDTO);
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		Resource resource = BeanUtil.copyProperties(resourceDTO,Resource.class);
		resource.setCreateBy(currentUser.getId());
		resource.setCreateTime(new Date());
		if (resourceService.save(resource)) {
			return Result.success("新增成功");
		}
		return Result.failed("新增失败");
	}

	@Operation(summary = "修改", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping(value={"/edit"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> edit(@Validated(value = EditGroup.class)  @RequestBody ResourceDTO groupDTO) {
		log.debug("-edit:{}",groupDTO);
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		Resource resource = BeanUtil.copyProperties(groupDTO,Resource.class);
		resource.setUpdateBy(currentUser.getId());
		resource.setUpdateTime(new Date());
		if (resourceService.updateById(resource)) {
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
		if (resourceService.removeBatchByIds(Arrays.asList(ids))) {
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
		if (resourceService.updateStatus(changeStatusDTO,currentUser.getId(),new Date())) {
			return Result.success("修改状态成功");
		}
		return Result.failed("修改状态失败");
	}



}
