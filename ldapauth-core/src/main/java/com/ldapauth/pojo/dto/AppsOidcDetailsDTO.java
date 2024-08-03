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
 * @Date: 2024/7/11  16:24
 */
@Data
@EqualsAndHashCode
public class AppsOidcDetailsDTO {

    @NotNull(message = "主建不能为空", groups = {EditGroup.class})
    @Schema(name = "appId", description = "应用ID")
    Long appId;

    @Schema(name = "clientId", description = "客户端ID")
    String clientId;

    @Schema(name = "clientSecret", description = "客户端密钥")
    String clientSecret;

    @Schema(name = "ipWhiteIds", description = "IP白名单集合")
    String ipWhiteIds;

    @NotBlank(message = "授权类型不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "authorizedGrantTypes", description = "授权类型")
    String authorizedGrantTypes;

    @NotBlank(message = "作用域不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "scope", description = "作用域")
    String scope;

    @NotBlank(message = "回调地址不能为空", groups = {AddGroup.class, EditGroup.class})
    @Size(min = 1, max = 1024, message = "回调地址长度必须介于1和1024之间")
    @Schema(name = "redirectUri", description = "回调地址")
    String redirectUri;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(name = "idTokenValidity", description = "IDToken有效时间(单位秒)")
    Long idTokenValidity;

    @Schema(name = "codeValidity", description = "code有效时间(单位秒)")
    Long codeValidity;

    @Schema(name = "accessTokenValidity", description = "accessToken有效时间(单位秒)")
    Long accessTokenValidity;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(name = "refreshTokenValidity", description = "refreshToken有效时间(单位秒)")
    Long refreshTokenValidity;

    @Schema(name = "additionalInformation", description = "扩展信息")
    String additionalInformation;

    @NotBlank(message = "响应主体不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "subject", description = "响应主体-username/mobile/email等")
    String subject;

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
