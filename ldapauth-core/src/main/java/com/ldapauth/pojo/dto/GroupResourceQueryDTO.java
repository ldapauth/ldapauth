package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.QueryGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Schema(name = "GroupResourceQueryDTO", description = "分组成员查询接口")
@EqualsAndHashCode(callSuper = false)
@Data
public class GroupResourceQueryDTO extends PageQueryDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@NotNull(message = "分组编码不能为空", groups = { QueryGroup.class})
	@Schema(name = "groupId", description = "分组编码")
	Long groupId;

	@Schema(name = "name", description = "资源名称")
	String name;

	@Schema(name = "parentId", description = "父级标识")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long parentId;

	@Schema(name = "classify", description = "类型 menu-菜单类型 button-按钮类型 api-接口类型")
	String classify;

	@Schema(name = "resAction", description = "资源操作 read-读操作 write-写操作 list-列表操作")
	String resAction;


}
