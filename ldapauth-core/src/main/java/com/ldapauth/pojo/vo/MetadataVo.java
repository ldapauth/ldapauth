package com.ldapauth.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "MetadataVo", description = "元数据解析DTO")
public class MetadataVo {

    @Schema(name = "certIssuer", description = "证书颁发者")
    String certIssuer;

    @Schema(name = "certSubject", description = "证书主题")
    String certSubject;

    @Schema(name = "certExpiration", description = "证书有效期")
    String certExpiration;

    @Schema(name = "b64Encoder", description = "b64Encoder")
    String b64Encoder;

    @Schema(name = "issuer", description = "issuer")
    String issuer;

    @Schema(name = "spAcsUri", description = "SP ACS URL(SSO Location)")
    String spAcsUri;

    @Schema(name = "entityId", description = "entityId")
    String entityId;


    @Schema(name = "audience", description = "audience")
    String audience;

    @Schema(name = "firstNameIDFormat", description = "支持属性")
    String firstNameIDFormat;


    @Schema(name = "firstBinding", description = "第一条支持请求方式支持")
    String firstBinding;

}
