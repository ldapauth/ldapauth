package com.ldapauth.openapi.rest;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.persistence.service.OrganizationService;
import com.ldapauth.pojo.dto.IdsListDto;
import com.ldapauth.pojo.dto.OrganizationDTO;
import com.ldapauth.pojo.dto.OrganizationQueryDTO;
import com.ldapauth.pojo.entity.Organization;
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
		name = "组织开放服务",
		description = "提供组织基础接口，包含新增/修改/删除/查询等基本接口"
)
@RestController
@RequestMapping(value = { "/openapi/idm/Organization" })
@Slf4j
public class RestOrganizationsController {

	@Autowired
	private OrganizationService organizationService;

	@Operation(summary = "分页查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/fetch" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<Page<Organization>> fetch(@ParameterObject OrganizationQueryDTO queryDTO) {
		log.debug("-fetch:{}",queryDTO);
		return Result.success(organizationService.fetch(queryDTO));
	}

	@Operation(summary = "ID查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/get/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<Organization> get(@Validated @NotNull(message = "ID不能为空") @PathVariable("id") Long id) {
		log.debug("Fetching organization with ID: {}", id);
		Organization organization = organizationService.getById(id);
		if (Objects.isNull(organization)) {
			log.warn("Organization with ID {} not found", id);
			return Result.failed("查询ID不存在");
		}

		if (organization.getParentId() != null && organization.getParentId().intValue() == 0) {
			organization.setParentId(null);
		}
		return Result.success(organization);
	}

	@Operation(summary = "新增", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value={"/add"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<String> add(@Validated(value = AddGroup.class) @RequestBody OrganizationDTO dto) {
		log.debug("-add:{}",dto);
		Organization organization = BeanUtil.copyProperties(dto,Organization.class);
		organization.setCreateBy(0L);
		organization.setCreateTime(new Date());
		return organizationService.saveOrg(organization);
	}

	@Operation(summary = "修改", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping(value={"/edit"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<String> edit(@Validated(value = EditGroup.class)  @RequestBody OrganizationDTO dto) {
		log.debug("-edit:{}",dto);
		Organization organization = BeanUtil.copyProperties(dto,Organization.class);
		organization.setUpdateBy(0L);
		organization.setUpdateTime(new Date());
		return organizationService.editOrg(organization);
	}

	@Operation(summary = "删除/批量删除", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@DeleteMapping("/delete")
	public Result<String> delete(@Validated @RequestBody IdsListDto idsListDto) {
		log.debug("删除-IdsListDto:{}",idsListDto);
		return (organizationService.deleteBatch(idsListDto));
	}

	@Operation(summary = "批量禁用", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping( "/disable" )
	public Result<String> disableBatch(@Validated @RequestBody IdsListDto idsListDto) {
		log.debug("禁用-IdsListDto:{}",idsListDto);
		return (organizationService.disableBatch(idsListDto,0L,new Date()));
	}

	@Operation(summary = "批量启用", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping("/active")
	public Result<String> activeBatch(@Validated @RequestBody IdsListDto idsListDto) {
		log.debug("启用-IdsListDto:{}",idsListDto);
		return (organizationService.activeBatch(idsListDto,0L,new Date()));
	}
}
