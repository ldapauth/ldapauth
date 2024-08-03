package com.ldapauth.web.contorller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.GroupResourceService;
import com.ldapauth.pojo.dto.GroupResourceQueryDTO;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.Group;
import com.ldapauth.pojo.entity.Resource;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.IdsVO;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.validate.EditGroup;
import com.ldapauth.validate.QueryGroup;
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
		name = "分组资源服务",
		description = "提供分组资源基础接口，包含新增/删除/查询等基本接口"
)
@RestController
@RequestMapping(value = { "/groups/resource" })
@Slf4j
public class GroupsResourceController {

	@Autowired
	private GroupResourceService groupResourceService;

	@Operation(summary = "分页查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/fetch" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<Page<Resource>> fetch(@ParameterObject @Validated(value = QueryGroup.class) GroupResourceQueryDTO queryDTO) {
		log.debug("-fetch:{}",queryDTO);
		return Result.success(groupResourceService.fetch(queryDTO));
	}


	@Operation(summary = "授权组资源", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value = { "/authResource" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> authResource(@Validated(value = EditGroup.class)
									@RequestBody IdsDTO idsDTO
	) {
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		log.debug("-addResource:{}",idsDTO);
		if (groupResourceService.authResource(idsDTO,currentUser.getId(),new Date())) {
			return Result.success("添加成功");
		}
		return Result.failed("添加失败");
	}

	@Operation(summary = "获取分组组已授权资源ID", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/getAuthResourceIds/{groupId}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<IdsVO> getAuthResourceIds(@Validated @NotNull(message = "groupId不能为空") @PathVariable("groupId") Long groupId) {
		log.debug("-getAuthResourceIds:{}",groupId);
		return Result.success(groupResourceService.getAuthResourceIds(groupId));
	}
}
