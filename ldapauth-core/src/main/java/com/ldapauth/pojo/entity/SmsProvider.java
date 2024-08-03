package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: orangeBabu
 * @time: 15/7/2024 AM9:34
 */

@EqualsAndHashCode(callSuper = true)
@TableName(value = "lda_sms_provider")
@Schema(name="SmsProvider",description ="短信配置" )
@Data
public class SmsProvider extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(name = "id",description = "ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;

    @Schema(name = "provider",description = "短信厂商")
    String provider;

    @Schema(name = "hostname",description = "服务地址")
    String hostname;

    @Schema(name = "appKey",description = "appKey")
    String appKey;

    @Schema(name = "appSecret",description = "appSecret")
    String appSecret;

    @Schema(name = "signName",description = "签名")
    String signName;

    @Schema(name = "status",description = "状态 0启用 1禁用")
    Integer status;

}
