package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Schema(name = "UserInfoQueryDTO", description = "查询参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class UserInfoQueryDTO extends PageQueryDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	@Schema(name = "objectFrom", description = "对象来源 ldap=ldap 或者 system=本地")
	String objectFrom;

	@Schema(name = "username", description = "登录账号")
	String username;

	@Schema(name = "gender", description = "性别:0-其他;1-男;2-女")
	Integer gender;

	@Schema(name = "displayName", description = "显示名称")
	String displayName;

	@Schema(name = "nickName", description = "昵称")
	String nickName;

	@Schema(name = "email", description = "邮箱")
	String email;

	@Schema(name = "mobile", description = "手机号码")
	String mobile;

	@Schema(name = "departmentId", description = "所属部门标识")
	Long departmentId;

	@Schema(name = "status", description = "状态: 0-启用; 1-禁用")
	Integer status;
}
