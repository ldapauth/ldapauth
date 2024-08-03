package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Schema(name = "SocialsProviderQueryDTO", description = "查询参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class SocialsProviderQueryDTO extends PageQueryDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	@Schema(name = "name", description = "名称")
	String name;

	@Schema(name = "status", description = "状态: 0-启用; 1-禁用")
	Integer status;


}
