package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Schema(name = "GroupQueryDTO", description = "查询参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class GroupQueryDTO extends PageQueryDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	@Schema(name = "objectFrom", description = "对象来源 ldap=ldap 或者 system=本地")
	String objectFrom;

	@Schema(name = "name", description = "组名称")
	String name;

	@Schema(name = "status", description = "状态: 0-启用; 1-禁用")
	Integer status;


}
