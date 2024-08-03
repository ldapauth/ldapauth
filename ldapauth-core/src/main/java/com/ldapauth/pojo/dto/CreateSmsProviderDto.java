package com.ldapauth.pojo.dto;

import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @description:
 * @author: orangeBabu
 * @time: 15/7/2024 AM9:55
 */

@Schema(name="CreateSmsProviderDto",description ="短信配置新增" )
@Data
public class CreateSmsProviderDto {

    @Schema(name = "provider",description = "短信厂商")
    @NotNull(message = "ID不能为空", groups = EditGroup.class)
    Long id;

    @Schema(name = "status",description = "状态")
    Integer status;

    @Schema(name = "provider",description = "短信厂商")
    @NotBlank(message = "短信厂商不能为空")
    @Size(max = 32, message = "短信厂商长度不超过32")
    String provider;

    @Schema(name = "hostname",description = "官网地址")
    @Size(max = 255, message = "官网地址长度不超过255")
    String hostname;

    @Schema(name = "appKey",description = "appKey")
    @NotBlank(message = "appKey不能为空")
    @Size(max = 64, message = "凭证长度不超过64")
    String appKey;

    @Schema(name = "appSecret",description = "appSecret")
    @Size(max = 64, message = "秘钥长度不超过64")
    @NotBlank(message = "appSecret不能为空")
    String appSecret;

    @Schema(name = "signName",description = "签名")
    @NotBlank(message = "签名不能为空")
    @Size(max = 50, message = "签名长度不超过50")
    String signName;
}
