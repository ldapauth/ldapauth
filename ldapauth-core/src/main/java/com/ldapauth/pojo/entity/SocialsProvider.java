package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@TableName("lda_socials_provider")
@Schema(name = "SocialsProvider", description = "第三方配置表")
@Data
public class SocialsProvider extends BaseEntity {

	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "icon", description = "图标")
	String icon;

	@Schema(name = "type", description = "类型")
	String type;

	@Schema(name = "name", description = "名称")
	String name;

	@Schema(name = "clientId", description = "凭证")
	String clientId;

	@Schema(name = "clientSecret", description = "密钥")
	String clientSecret;

	@Schema(name = "agentId", description = "agentId")
	String agentId;


	@Schema(name = "alipayPublicKey", description = "支付宝公钥")
	String alipayPublicKey;


	@Schema(name = "redirectUri", description = "回调地址")
	String redirectUri;


	@Schema(name = "description", description = "描述")
	String description;


	@Schema(name = "status", description = "状态 0启用 1禁用")
	Integer status;

}
