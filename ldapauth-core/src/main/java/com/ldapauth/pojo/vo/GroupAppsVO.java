package com.ldapauth.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@Schema(name = "GroupAppsVO", description = "分组应用对象")
@EqualsAndHashCode(callSuper = false)
@Data
public class GroupAppsVO  {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Schema(name = "id", description = "授权ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "groupId", description = "分组ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long groupId;

	@Schema(name = "groupName", description = "分组名称")
	String groupName;


	@Schema(name = "appId", description = "应用编码")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long appId;

	@Schema(name = "appName", description = "应用名称")
	String appName;

	/**
	 * 创建者
	 */
	@Schema(name = "createBy",description = "创建人")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long createBy;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Schema(name = "createTime",description = "创建时间")
	Date createTime;


}
