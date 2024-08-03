package com.ldapauth.web.contorller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.persistence.service.SynchronizersLogsService;
import com.ldapauth.pojo.dto.SynchronizersLogsQueryDTO;
import com.ldapauth.pojo.entity.SynchronizersLogs;
import com.ldapauth.pojo.vo.Result;
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
import java.util.Objects;

/**
 * @author Shi.bl
 *
 */
@Tag(
		name = "同步器日志服务",
		description = "提供同步器日志查询等基本接口"
)
@RestController
@RequestMapping(value = { "/synchronizers/logs" })
@Slf4j
public class SynchronizersLogsController {

	@Autowired
	private SynchronizersLogsService synchronizersLogsService;

	@Operation(summary = "分页查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/fetch" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<Page<SynchronizersLogs>> fetch(@ParameterObject SynchronizersLogsQueryDTO queryDTO) {
		log.debug("-fetch:{}",queryDTO);
		return Result.success(synchronizersLogsService.fetch(queryDTO));
	}


	@Operation(summary = "ID查询", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value = { "/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<SynchronizersLogs> get(@Validated @NotNull(message = "ID不能为空") @PathVariable("id") Long id) {
		log.debug("-get:{}",id);
		SynchronizersLogs data = synchronizersLogsService.getById(id);
		if (Objects.nonNull(data)) {
			return Result.success(data);
		}
		return Result.failed("查询ID不存在");
	}





}
