package com.ldapauth.web.contorller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.GroupMemberService;
import com.ldapauth.persistence.service.GroupService;
import com.ldapauth.pojo.dto.GroupMemberQueryDTO;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.Group;
import com.ldapauth.pojo.entity.UserInfo;
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
import java.util.Date;

/**
 * @author Shi.bl
 *
 */
@Tag(
		name = "分组成员服务",
		description = "提供分组成员基础接口，包含新增/删除/查询等基本接口"
)
@RestController
@RequestMapping(value = { "/groups/member" })
@Slf4j
public class GroupsMemberController {

	@Autowired
	GroupMemberService groupMemberService;
	@Autowired
	GroupService groupService;

	@Operation(summary = "分页查询-已授权用户", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/fetchAuthList" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<Page<UserInfo>> fetchAuthList(@ParameterObject @Validated(value = QueryGroup.class) GroupMemberQueryDTO queryDTO) {
		log.debug("-fetch:{}",queryDTO);
		return Result.success(groupMemberService.fetchAuthList(queryDTO));
	}

	@Operation(summary = "分页查询-未授权用户", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/fetchNotAuthList" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<Page<UserInfo>> fetchNotAuthList(@ParameterObject @Validated(value = QueryGroup.class) GroupMemberQueryDTO queryDTO) {
		log.debug("-fetch:{}",queryDTO);
		return Result.success(groupMemberService.fetchNotAuthList(queryDTO));
	}

	@Operation(summary = "添加成员", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value = { "/addMember" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> addMember(@Validated(value = EditGroup.class)
									   @RequestBody IdsDTO idsDTO
	) {
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		log.debug("-addMember:{}",idsDTO);
		if (groupMemberService.addMember(idsDTO,currentUser.getId(),new Date())) {
			for (Long groupId : idsDTO.getIds()) {
				//通知组更新成员关系
				Group group = groupService.getById(groupId);
				groupService.updateById(group);
			}
			return Result.success("添加成功");
		}
		return Result.failed("添加失败");
	}


	@Operation(summary = "移除成员", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value = { "/removeMember" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> removeMember(@Validated(value = EditGroup.class)
									@RequestBody IdsDTO idsDTO
	) {
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		log.debug("-removeMember:{}",idsDTO);
		if (groupMemberService.removeMember(idsDTO,currentUser.getId(),new Date())) {
			for (Long groupId : idsDTO.getIds()) {
				//通知组更新成员关系
				Group group = groupService.getById(groupId);
				groupService.updateById(group);
			}
			return Result.success("移除成功");
		}
		return Result.failed("移除失败");
	}

}
