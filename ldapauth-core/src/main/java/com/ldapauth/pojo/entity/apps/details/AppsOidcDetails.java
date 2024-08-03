package com.ldapauth.pojo.entity.apps.details;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "lda_apps_oidc_details")
@Schema(name = "AppsOidcDetails", description = "OAUTH扩展表")
@EqualsAndHashCode(callSuper = false)
@Data
public class AppsOidcDetails extends BaseEntity {

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

	@TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
	@Schema(name = "clientId", description = "客户端ID")
	String clientId;

	@TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
	@Schema(name = "clientSecret", description = "客户端密钥")
	String clientSecret;

	@Schema(name = "ipWhiteIds", description = "IP白名单集合")
	String ipWhiteIds;

	@Schema(name = "scope", description = "作用域")
	String scope;

	@Schema(name = "authorizedGrantTypes", description = "授权类型")
	String authorizedGrantTypes;

	@Schema(name = "redirectUri", description = "回调地址")
	String redirectUri;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Schema(name = "codeValidity", description = "code有效时间(单位秒)")
	Long codeValidity;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Schema(name = "accessTokenValidity", description = "accessToken有效时间(单位秒)")
	Long accessTokenValidity;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Schema(name = "refreshTokenValidity", description = "refreshToken有效时间(单位秒)")
	Long refreshTokenValidity;

	@Schema(name = "additionalInformation", description = "扩展信息")
	String additionalInformation;

	@Schema(name = "subject", description = "响应主体-username/mobile/email等")
	String subject;

	@Schema(name = "responseMode", description = "responseMode query/fragment/form_post等")
	String responseMode;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Schema(name = "idTokenValidity", description = "IDToken有效时间(单位秒)")
	Long idTokenValidity;

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

	@Schema(name = "status", description = "状态 0正常 1禁用")
	Integer status;




}
