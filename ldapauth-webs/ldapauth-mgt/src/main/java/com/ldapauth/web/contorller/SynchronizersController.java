package com.ldapauth.web.contorller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.FileUploadService;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.pojo.dto.ChangeStatusDTO;
import com.ldapauth.pojo.dto.SynchronizersDTO;
import com.ldapauth.pojo.entity.FileUpload;
import com.ldapauth.pojo.entity.Synchronizers;
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
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Objects;

/**
 * @author Shi.bl
 *
 */
@Tag(
		name = "同步器服务",
		description = "提供同步器基础接口，包含新增/修改/删除/查询等基本接口"
)
@RestController
@RequestMapping(value = { "/synchronizers" })
@Slf4j
public class SynchronizersController {

	@Autowired
	private SynchronizersService synchronizersService;

	@Autowired
	private FileUploadService fileUploadService;


	/**
	 * 定义证书路径
	 */
	final static String JKS_FILE = "/app/ldapauth/trustStore";
	/**
	 * 定义默认密码
	 */
	final static String JKS_FILE_PASSWORD = "changeit";

	@Operation(summary = "ID查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/get/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<Synchronizers> get(@Validated @NotNull(message = "ID不能为空") @PathVariable("id") Long id) {
		log.debug("-get:{}",id);
		Synchronizers group = synchronizersService.getById(id);
		if (Objects.nonNull(group)) {
			return Result.success(group);
		}
		return Result.failed("查询ID不存在");
	}

	@Operation(summary = "修改", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PutMapping(value={"/edit"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> edit(@Validated(value = EditGroup.class)  @RequestBody SynchronizersDTO synchronizersDTO) {
		log.debug("-edit:{}",synchronizersDTO);
		UserInfo currentUser = AuthorizationUtils.getUserInfo();
		Synchronizers synchronizer = BeanUtil.copyProperties(synchronizersDTO,Synchronizers.class);
		//判断是否开启ssl
		if (Objects.nonNull(synchronizersDTO.isOpenssl()) && synchronizer.getOpenssl().booleanValue()) {
			//返回路径
			synchronizer.setTrustStore(JKS_FILE);
			synchronizer.setTrustStorePassword(JKS_FILE_PASSWORD);
		}
		synchronizer.setUpdateBy(currentUser.getId());
		synchronizer.setUpdateTime(new Date());
		if (synchronizersService.updateById(synchronizer)) {
			return Result.success("修改成功");
		}
		return Result.failed("修改失败");
	}

	@Operation(summary = "测试配置", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value={"/test"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> test(@Validated(value = EditGroup.class)  @RequestBody SynchronizersDTO synchronizersDTO) {
		log.debug("-test:{}",synchronizersDTO);
		Synchronizers synchronizers = BeanUtil.copyProperties(synchronizersDTO,Synchronizers.class);
		synchronizers.setTrustStorePassword(JKS_FILE_PASSWORD);
		synchronizers.setTrustStore(JKS_FILE);
		if (synchronizersService.test(synchronizers)) {
			return Result.success("测试成功");
		}
		return Result.failed("测试失败");
	}

	@Operation(summary = "立即同步", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@PostMapping(value = { "/sync/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<String> sync(@Validated @NotNull(message = "ID不能为空") @PathVariable("id") Long id) {
		log.debug("-sync:{}",id);
		if (synchronizersService.sync(id)) {
			return Result.success("正在处理");
		}
		return Result.failed("测试失败");
	}


}
