package com.ldapauth.pojo.entity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/23  10:10
 */
@TableName(value = "lda_app_login_log")
@Schema(name="AppLoginLog",description ="访问应用")
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppLoginLog {

    @Schema(description = "日志ID")
    Long id;

    @Schema(description = "用户ID")
    Long userId;

    @Schema(description = "用户显示名称")
    String displayName;

    @Schema(description = "应用ID")
    Long appId;

    @Schema(description = "应用名称")
    String appName;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Schema(name = "createTime",description = "创建时间")
    Date createTime;
}
