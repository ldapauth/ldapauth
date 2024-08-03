package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Schema(name = "GroupAppsQueryDTO", description = "分组应用查询接口")
@EqualsAndHashCode(callSuper = false)
@Data
public class GroupAppsQueryDTO extends PageQueryDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	@Schema(name = "groupName", description = "分组名称")
	String groupName;

	@Schema(name = "appName", description = "应用名称")
	String appName;



}
