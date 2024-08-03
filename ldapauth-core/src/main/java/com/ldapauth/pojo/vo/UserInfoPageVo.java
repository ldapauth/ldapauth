package com.ldapauth.pojo.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: orangeBabu
 * @time: 11/7/2024 PM12:04
 */

@Data
public class UserInfoPageVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;

    @Schema(name = "objectFrom", description = "对象来源 ldap=ldap 或者 system=本地")
    String objectFrom;

    @Schema(name = "ldapDn", description = "ldap 路径（ldapdn）")
    String ldapDn;

    @Schema(name = "username", description = "登录账号")
    String username;

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

    @Schema(name = "departmentNamePath", description = "所属部门路径")
    String departmentNamePath;

    @Schema(name = "description", description = "描述")
    String description;

    @Schema(name = "status", description = "状态: 0-启用; 1-禁用")
    Integer status;

    @Schema(name = "isLocked", description = "锁定状态")
    Integer isLocked;

    @Schema(name = "unLockTime", description = "解锁时间")
    @JsonFormat(
            pattern = "yyyy-MM-dd",
            timezone = "GMT+8"
    )
    Date unLockTime;

    @Schema(name = "birthDate", description = "出生日期")
    @JsonFormat(
            pattern = "yyyy-MM-dd",
            timezone = "GMT+8"
    )
    Date birthDate;

    @Schema(name = "openId", description = "开放ID")
    String openId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Schema(name = "createTime",description = "创建时间")
    Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Schema(name = "updateTime",description = "更新时间")
    Date updateTime;
}
