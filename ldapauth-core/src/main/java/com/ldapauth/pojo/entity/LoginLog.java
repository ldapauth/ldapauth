package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.protobuf.FieldType;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/23  10:09
 */
@TableName(value = "lda_login_log")
@Schema(name="LoginLog",description ="登录日志")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LoginLog  {

    @Schema(description = "日志ID")
    Long id;

    @Schema(description = "用户ID")
    Long userId;

    @Schema(description = "用户显示名称")
    String displayName;

    @Schema(description = "登录方式")
    String loginType;

    @Schema(description = "提供商")
    String provider;

    @Schema(description = "操作系统")
    String operateSystem;

    @Schema(description = "浏览器")
    String browser;

    @Schema(description = "ip地址")
    String ipAddr;

    @Schema(description = "国家")
    String country;

    @Schema(description = "省")
    String province;



    @Schema(description = "市")
    String city;

    @Schema(description = "状态 0成功 1失败")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    int status;

    @Schema(description = "日志内容")
    String message;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Schema(name = "createTime",description = "创建时间")
    Date createTime;
}
