package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "lda_policy_login")
@Schema(name="PolicyLogin",description ="登录安全策略")
@Data
public class PolicyLogin extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(name = "id",description = "ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;

    @Schema(name = "refreshTokenValidity",description = "refresh token有效期")
    Integer refreshTokenValidity;

    @Schema(name = "accessTokenValidity",description = "token有效期")
    Integer accessTokenValidity;

    @Schema(name = "isCaptcha",description = "图形验证码 0开启 1关闭")
    Integer isCaptcha;

    @Schema(name = "passwordAttempts",description = "密码输入错误次数")
    Integer passwordAttempts;

    @Schema(name = "loginLockInterval",description = "锁定时间（分钟）")
    Integer loginLockInterval;


}
