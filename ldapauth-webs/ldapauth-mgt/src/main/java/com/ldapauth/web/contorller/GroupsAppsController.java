package com.ldapauth.web.contorller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.GroupAppsService;
import com.ldapauth.pojo.dto.GroupAppsQueryDTO;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.GroupAppsVO;
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

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Shi.bl
 *
 */
@Tag(
		name = "分组应用",
		description = "提供分组应用基础接口，包含新增/删除/查询等基本接口"
)
@RestController
@RequestMapping(value = { "/groups/apps" })
@Slf4j
public class GroupsAppsController {

	@Autowired
	private GroupAppsService groupAppsService;

	@Operation(summary = "分页查询-已授权列表", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/fetch" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<Page<GroupAppsVO>> fetchAuthList(@ParameterObject @Validated(value = QueryGroup.class) GroupAppsQueryDTO queryDTO) {
		log.debug("-fetch:{}",queryDTO);
		return Result.success(groupAppsService.fetch(queryDTO));
	}


	@Operation(summary = "添加授权", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value = { "/auth" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> auth(@Validated(value = EditGroup.class)
									   @RequestBody IdsDTO idsDTO
	) {
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		log.debug("-auth:{}",idsDTO);
		if (groupAppsService.authApps(idsDTO,currentUser.getId(),new Date())) {
			return Result.success("授权成功");
		}
		return Result.failed("授权失败");
	}


	@Operation(summary = "移除授权", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@DeleteMapping(value={"/delete/{ids}"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> removeAuth(@NotEmpty(message = "ID集合不能为空") @PathVariable Long[] ids) {
		log.debug("-removeAuth:{}",ids);
		if (groupAppsService.removeBatchByIds(Arrays.asList(ids))) {
			return Result.success("移除成功");
		}
		return Result.failed("移除失败");
	}

}
