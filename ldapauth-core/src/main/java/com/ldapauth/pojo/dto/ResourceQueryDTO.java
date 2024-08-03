package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Schema(name = "ResourceQueryDTO", description = "查询参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class ResourceQueryDTO extends PageQueryDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Schema(name = "name", description = "资源名称")
	String name;

	@Schema(name = "parentId", description = "父级标识")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long parentId;

	@Schema(name = "classify", description = "类型 menu-菜单类型 button-按钮类型 api-接口类型")
	String classify;

	@Schema(name = "resAction", description = "资源操作 read-读操作 write-写操作 list-列表操作")
	String resAction;

	@Schema(name = "appId", description = "应用ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long appId;

	@Schema(name = "status", description = "状态: 0-启用; 1-禁用")
	Integer status;


}
