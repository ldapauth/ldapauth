package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Schema(name = "OrganizationQueryDTO", description = "查询参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class OrganizationQueryDTO extends PageQueryDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	@Schema(name = "id", description = "所选部门")
	Long id;

	@Schema(name = "objectFrom", description = "来源 ldap=ldap 或者 system=本地")
	String objectFrom;


	@Schema(name = "ldapDn", description = "ldap 路径（ldapdn）")
	String ldapDn;

	@Schema(name = "orgName", description = "组名称")
	String orgName;

	@Schema(name = "openDepartmentId", description = "对外开放ID")
	String openDepartmentId;

	@Schema(name = "parentId", description = "父级ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long parentId;

	@Schema(name = "status", description = "状态")
	Integer status;


}
