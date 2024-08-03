package com.ldapauth.web.contorller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.SocialsProviderService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.SocialsProvider;
import com.ldapauth.pojo.entity.UserInfo;
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
		name = "第三方服务",
		description = "提供基础接口，包含新增/修改/删除/查询等基本接口"
)
@RestController
@RequestMapping(value = { "/socials-provider" })
@Slf4j
public class SocialsProviderController {

	@Autowired
	private SocialsProviderService socialsProviderService;


	@Operation(summary = "生成ID", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value={"/generate"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> generate() {
		log.debug("-generate");
		return Result.success(socialsProviderService.generate());
	}

	@Operation(summary = "分页查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/fetch" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<Page<SocialsProvider>> fetch(@ParameterObject SocialsProviderQueryDTO queryDTO) {
		log.debug("-fetch:{}",queryDTO);
		return Result.success(socialsProviderService.fetch(queryDTO));
	}

	@Operation(summary = "ID查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/get/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<SocialsProvider> get(@Validated @NotNull(message = "ID不能为空") @PathVariable("id") Long id) {
		log.debug("-get:{}",id);
		SocialsProvider provider = socialsProviderService.getById(id);
		if (Objects.nonNull(provider)) {
			return Result.success(provider);
		}
		return Result.failed("查询ID不存在");
	}

	@Operation(summary = "新增", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value={"/add"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> add(@Validated(value = AddGroup.class) @RequestBody SocialsProviderDTO dto) {
		log.debug("-add:{}",dto);
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		SocialsProvider provider = BeanUtil.copyProperties(dto,SocialsProvider.class);
		provider.setCreateBy(currentUser.getId());
		provider.setCreateTime(new Date());
		if (socialsProviderService.saveOrUpdate(provider)) {
			return Result.success("新增成功");
		}
		return Result.failed("新增失败");
	}

	@Operation(summary = "修改", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping(value={"/edit"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> edit(@Validated(value = EditGroup.class)  @RequestBody SocialsProviderDTO dto) {
		log.debug("-edit:{}",dto);
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		SocialsProvider provider = BeanUtil.copyProperties(dto,SocialsProvider.class);
		provider.setUpdateBy(currentUser.getId());
		provider.setUpdateTime(new Date());
		if (socialsProviderService.saveOrUpdate(provider)) {
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
		if (socialsProviderService.removeBatchByIds(Arrays.asList(ids))) {
			return Result.success("删除成功");
		}
		return Result.failed("删除失败");
	}

}
