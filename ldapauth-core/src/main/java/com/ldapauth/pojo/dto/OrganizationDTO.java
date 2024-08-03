package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Schema(name = "OrganizationDTO", description = "新增或者修改参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class OrganizationDTO {

	@NotNull(message = "ID不能为空", groups = { EditGroup.class})
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "objectFrom", description = "对象来源 ldap=ldap 或者 system=本地")
	String objectFrom;

	@Schema(name = "ldapDn", description = "ldap 路径（ldapdn）")
	String ldapDn;

	@NotNull(groups = {AddGroup.class, EditGroup.class},message = "组织名称不能为空")
	@Schema(name = "orgName", description = "组名称")
	String orgName;

	@Schema(name = "openDepartmentId", description = "对外开放ID")
	String openDepartmentId;

	@Schema(name = "classify", description = "分类")
	String classify;

	@Schema(name = "parentId", description = "父级ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long parentId;

	@Schema(name = "description", description = "描述")
	String description;

	@Schema(name = "status", description = "状态")
	Integer status;

	@Schema(name = "sortIndex", description = "排序")
	Integer sortIndex;

}
