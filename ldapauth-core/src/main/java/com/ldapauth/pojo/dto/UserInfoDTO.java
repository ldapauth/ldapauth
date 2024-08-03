package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Schema(name = "UserInfoDTO", description = "新增或者修改参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class UserInfoDTO  {

	@NotNull(message = "用户ID不能为空", groups = { EditGroup.class})
	@Schema(name = "id", description = "用户标识")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "objectFrom", description = "对象来源 ldap=ldap 或者 system=本地")
	String objectFrom;

	@Schema(name = "ldapDn", description = "ldap 路径（ldapdn）")
	String ldapDn;

	@NotNull(groups = {AddGroup.class},message = "登录账号不能为空")
	@Schema(name = "username", description = "登录账号")
	String username;

	@NotNull(groups = AddGroup.class,message = "密码不能为空")
	@Schema(name = "password", description = "密码")
	String password;

	@Schema(name = "gender", description = "性别:0-其他;1-男;2-女")
	Integer gender;

	@NotNull(groups = {AddGroup.class, EditGroup.class},message = "显示名称不能为空")
	@Schema(name = "displayName", description = "显示名称")
	String displayName;

	@Schema(name = "nickName", description = "昵称")
	String nickName;

	@Schema(name = "avatar", description = "头像")
	String avatar;

	@Schema(name = "email", description = "邮箱")
	String email;

	@Schema(name = "mobile", description = "手机号")
	String mobile;

	@NotNull(groups = {AddGroup.class, EditGroup.class},message = "所属部门不能为空")
	@Schema(name = "departmentId", description = "所属部门标识")
	Long departmentId;

	@Schema(name = "isLocked", description = "锁定状态")
	Integer isLocked;

	@Schema(name = "description", description = "描述")
	String description;

//	@NotNull(groups = {AddGroup.class, EditGroup.class},message = "状态不能为空")
	@Schema(name = "status", description = "状态: 0-启用; 1-禁用")
	Integer status;

	@Schema(name = "birthDate", description = "出生日期")
	@JsonFormat(
			pattern = "yyyy-MM-dd",
			timezone = "GMT+8"
	)
	Date birthDate;
}
