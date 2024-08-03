package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.crypto.Base64Utils;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * @description:
 * @author: 24096
 * @time: 2/7/2023 AM10:43
 */


@TableName(value = "lda_userinfo")
@Schema(name = "UserInfo", description = "用户表")
@EqualsAndHashCode(callSuper = false)
@Data
public class UserInfo extends BaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "用户标识")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "objectFrom", description = "对象来源 ldap=ldap 或者 system=本地")
	String objectFrom;

	@Schema(name = "ldapDn", description = "ldap 路径（ldapdn）")
	String ldapDn;

	@Schema(name = "username", description = "登录账号")
	String username;

	@Schema(name = "password", description = "密码")
	String password;

	@Schema(name = "decipherable", description = "DE密码")
	String decipherable;

	@Schema(name = "gender", description = "性别:0-其他;1-男;2-女")
	Integer gender;

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


	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Schema(name = "departmentId", description = "所属部门标识")
	Long departmentId;


	@TableField(exist = false)
	@Schema(name = "departmentName", description = "所属部门名称")
	String departmentName;

	@Schema(name = "isLocked", description = "锁定状态")
	Integer isLocked;

	@Schema(name = "unLockTime", description = "解锁时间")
	@JsonFormat(
			pattern = "yyyy-MM-dd",
			timezone = "GMT+8"
	)
	Date unLockTime;

	@Schema(name = "badPasswordCount", description = "坏密码次数")
	Integer badPasswordCount;

	@Schema(name = "badPasswordTime", description = "坏密码时间")
	@JsonFormat(
			pattern = "yyyy-MM-dd",
			timezone = "GMT+8"
	)
	Date badPasswordTime;

	@Schema(name = "passwordLastSetTime", description = "密码最后修改时间")
	@JsonFormat(
			pattern = "yyyy-MM-dd",
			timezone = "GMT+8"
	)
	Date passwordLastSetTime;

	@Schema(name = "loginCount", description = "登录次数")
	Integer loginCount;

    @Schema(name = "lastLoginTime", description = "最近登录时间")
	@JsonFormat(
			pattern = "yyyy-MM-dd",
			timezone = "GMT+8"
	)
    Date lastLoginTime;

	@Schema(name = "lastLoginIp", description = "最后登录IP")
	String lastLoginIp;

    @Schema(name = "loginFailedCount", description = "登录失败次数")
    Integer loginFailedCount;

    @Schema(name = "loginFailedTime", description = "登录失败时间")
	@JsonFormat(
			pattern = "yyyy-MM-dd",
			timezone = "GMT+8"
	)
    Date loginFailedTime;


	@Schema(name = "description", description = "描述")
	String description;

	@Schema(name = "status", description = "状态: 0-启用; 1-禁用")
	Integer status;

	@Schema(name = "birthDate", description = "出生日期")
	@JsonFormat(
			pattern = "yyyy-MM-dd",
			timezone = "GMT+8"
	)
	Date birthDate;

	@Schema(name = "openId", description = "开放ID")
	String openId;


	@Schema(name = "ldapId", description = "LDAP唯一标识（entryUUID）")
	String ldapId;

	public void trans() {
		this.setPassword("");
	}
}
