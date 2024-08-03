package com.ldapauth.pojo.entity.apps.details;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.security.cert.X509Certificate;

@TableName(value = "lda_apps_saml_v20_details")
@Schema(name = "AppsSamlDetails", description = "SAML扩展表")
@EqualsAndHashCode(callSuper = false)
@Data
public class AppsSamlDetails extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "id")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@TableField(exist = false)
	@Schema(name = "deviceType", description = "设备类型，0=移动端 1=PC端 2=WEB端")
	Integer deviceType;

	@Schema(name = "appId", description = "应用ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long appId;

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

	@Schema(name = "spAcsUri", description = "Sp Acs Uri")
	String spAcsUri;

	@Schema(name = "issuer", description = "issuer")
	String issuer;

	@Schema(name = "entityId", description = "entityId")
	String entityId;

	@Schema(name = "nameIdFormat", description = "nameIdFormat")
	String nameIdFormat;

	@Schema(name = "nameIdConvert", description = "nameIdConvert")
	String nameIdConvert;

	@Schema(name = "nameIdSuffix", description = "nameIdSuffix")
	String nameIdSuffix;

	@Schema(name = "audience", description = "受众(Audience)")
	String audience;

	@Schema(name = "encrypted", description = "encrypted")
	String encrypted;

	@Schema(name = "binding", description = "binding")
	String binding;

	@Schema(name = "signature", description = "signature")
	String signature;

	@Schema(name = "metaUri", description = "metaUri")
	String metaUri;

	@Schema(name = "digestMethod", description = "digestMethod")
	String digestMethod;


	@Schema(name = "ipWhiteIds", description = "IP白名单集合")
	String ipWhiteIds;

	@Schema(name = "metaType", description = "元数据类型")
	String metaType;

	@Schema(name = "status", description = "状态 0正常 1禁用")
	Integer status;

	@TableField(exist = false)
	X509Certificate trustCert = null;

}
