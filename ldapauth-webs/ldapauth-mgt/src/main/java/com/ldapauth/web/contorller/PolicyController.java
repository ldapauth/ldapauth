package com.ldapauth.web.contorller;

import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.PolicyLoginService;
import com.ldapauth.persistence.service.PolicyPasswordService;
import com.ldapauth.pojo.entity.PolicyLogin;
import com.ldapauth.pojo.entity.PolicyPassword;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;

/**
 * @author Shi.bl
 *
 */
@Tag(
		name = "策略服务",
		description = "提供密码和登录策略相关基础接口"
)
@RestController
@RequestMapping(value = { "/policy" })
@Slf4j
public class PolicyController {

	@Autowired
	private PolicyLoginService policyLoginService;

	@Autowired
	private PolicyPasswordService policyPasswordService;


	@Operation(summary = "获取密码策略", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/password" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<PolicyPassword> getPolicyPassword() {
		log.debug("-password");
		PolicyPassword policy = policyPasswordService.get();
		if (Objects.nonNull(policy)) {
			return Result.success(policy);
		}
		return Result.failed("查询不存在");
	}


	@Operation(summary = "获取登录策略", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/login" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<PolicyLogin> getPolicyLogin() {
		log.debug("-login");
		PolicyLogin policy = policyLoginService.get();
		if (Objects.nonNull(policy)) {
			return Result.success(policy);
		}
		return Result.failed("查询不存在");
	}

	@Operation(summary = "保存密码策略", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value={"/save/password"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> edit(@Validated(value = EditGroup.class)  @RequestBody PolicyPassword policyPassword) {
		log.debug("-save password:{}",policyPassword);
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		policyPassword.setUpdateBy(currentUser.getId());
		policyPassword.setUpdateTime(new Date());
		if (policyPasswordService.saveOrUpdate(policyPassword)) {
			return Result.success("保存成功");
		}
		return Result.failed("保存失败");
	}

	@Operation(summary = "保存登录策略", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value={"/save/login"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> edit(@Validated(value = EditGroup.class)  @RequestBody PolicyLogin policyLogin) {
		log.debug("-save login:{}",policyLogin);
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		policyLogin.setUpdateBy(currentUser.getId());
		policyLogin.setUpdateTime(new Date());
		if (policyLoginService.saveOrUpdate(policyLogin)) {
			return Result.success("保存成功");
		}
		return Result.failed("保存失败");
	}
}
