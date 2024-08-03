package com.ldapauth.pojo.dto;

import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/11  16:43
 */
@Data
@EqualsAndHashCode
public class AppsSamlDetailsDTO {
    @NotNull(message = "主建不能为空", groups = {EditGroup.class})
    @Schema(name = "appId", description = "应用ID")
    Long appId;

    @NotBlank(message = "响应主体不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "subject", description = "响应主体-username/mobile/email等")
    String subject;

    @Schema(name = "certIssuer", description = "证书颁发者")
    String certIssuer;

    @Schema(name = "certSubject", description = "证书主题")
    String certSubject;

    @Schema(name = "certExpiration", description = "证书过期时间")
    String certExpiration;

    @Schema(name = "keystore", description = "证书对象")
    byte[] keystore;


    @NotBlank(message = "SP ACS URL(SSO Location)不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "spAcsUri", description = "SP ACS URL(SSO Location)")
    String spAcsUri;

    @NotBlank(message = "Issuer不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "issuer", description = "issuer")
    String issuer;

    @NotBlank(message = "EntityId不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "entityId", description = "entityId")
    String entityId;

    @NotBlank(message = "nameIdFormat不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "nameIdFormat", description = "nameIdFormat")
    String nameIdFormat;

    @Schema(name = "nameIdConvert", description = "nameIdConvert")
    String nameIdConvert;

    @Schema(name = "nameIdSuffix", description = "nameIdSuffix")
    String nameIdSuffix;

    @NotBlank(message = "Audience不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "audience", description = "audience")
    String audience;

    @NotBlank(message = "Encrypted不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "encrypted", description = "encrypted")
    String encrypted;

    @NotBlank(message = "Binding不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "binding", description = "binding")
    String binding;

    @Schema(name = "signature", description = "signature")
    String signature;

    @Schema(name = "metaUri", description = "metaUri")
    String metaUri;

    @Schema(name = "digestMethod", description = "digestMethod")
    String digestMethod;

    @Schema(name = "metaType", description = "元数据(matadata)")
    String metaType;

    @Schema(name = "b64Encoder", description = "元数据对象b64Encoder")
    String b64Encoder;

}
