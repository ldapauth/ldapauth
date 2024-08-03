package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @description:
 * @author: orangeBabu
 * @time: 12/7/2024 PM2:56
 */

@Schema(name="SmsAliyunDto",description ="发送阿里云短信参数" )
@Data
public class SmsAliyunDto {

    @Schema(name = "templateId",description = "阿里云模板ID",requiredMode = Schema.RequiredMode.REQUIRED)
    String templateId;

    @Schema(name = "templateParam",description = "模板动态参数",requiredMode = Schema.RequiredMode.REQUIRED)
    Map<String, String> templateParam;

    @Schema(name = "mobile",description = "手机号码",requiredMode = Schema.RequiredMode.REQUIRED)
    String mobile;
}
