package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/11  16:45
 */
@Data
@EqualsAndHashCode
public class AppsJwtDetailsDTO {

    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    @Schema(name = "appId", description = "应用ID")
    Long appId;

    @NotBlank(message = "响应主体不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "subject", description = "响应主体-username/mobile/email等")
    String subject;

    @NotBlank(message = "回调地址不能为空", groups = {AddGroup.class, EditGroup.class})
    @Size(min = 1, max = 1024, message = "回调地址长度必须介于1和1024之间")
    @Schema(name = "redirectUri", description = "回调地址")
    String redirectUri;

    @Schema(name = "ssoBinding", description = "单点登陆请求方式 POST/GET")
    String ssoBinding;

    @Schema(name = "ipWhiteIds", description = "IP白名单集合")
    String ipWhiteIds;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(name = "idTokenValidity", description = "IDToken有效时间(单位秒)")
    Long idTokenValidity;

    @Schema(name = "jwtName", description = "jwtName")
    String jwtName;

    @Schema(name = "isSignature", description = "是否签名 yes签名 no不签名")
    String isSignature;

    @Schema(name = "signature", description = "签名方法")
    String signature;

    @Schema(name = "signatureKey", description = "签名密钥")
    String signatureKey;

    @Schema(name = "isAlgorithm", description = "是否加密 yes加密 no不签名")
    String isAlgorithm;

    @Schema(name = "algorithm", description = "加密算法")
    String algorithm;

    @Schema(name = "algorithmMethod", description = "加密方法")
    String algorithmMethod;

    @Schema(name = "algorithmKey", description = "加密key")
    String algorithmKey;

    @Schema(name = "issuer", description = "签发人")
    String issuer;

}