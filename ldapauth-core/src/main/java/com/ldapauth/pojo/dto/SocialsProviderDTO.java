package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Schema(name = "SocialsProviderDTO", description = "新增或者修改参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class SocialsProviderDTO {

	@NotNull(message = "ID不能为空", groups = { EditGroup.class})
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@NotNull(message = "图标不能为空", groups = { AddGroup.class,EditGroup.class})
	@Schema(name = "icon", description = "图标")
	String icon;

	@NotNull(message = "类型不能为空", groups = { AddGroup.class,EditGroup.class})
	@Schema(name = "type", description = "类型")
	String type;

	@NotNull(message = "名称不能为空", groups = { AddGroup.class,EditGroup.class})
	@Schema(name = "name", description = "名称")
	String name;

	@NotNull(message = "凭证不能为空", groups = { AddGroup.class,EditGroup.class})
	@Schema(name = "clientId", description = "凭证")
	String clientId;

	@NotNull(message = "密钥不能为空", groups = { AddGroup.class,EditGroup.class})
	@Schema(name = "clientSecret", description = "密钥")
	String clientSecret;

	@Schema(name = "agentId", description = "agentId")
	String agentId;


	@Schema(name = "alipayPublicKey", description = "支付宝公钥")
	String alipayPublicKey;

	@NotNull(message = "回调地址不能为空", groups = { AddGroup.class,EditGroup.class})
	@Schema(name = "redirectUri", description = "回调地址")
	String redirectUri;

	@Schema(name = "description", description = "描述")
	String description;

	@Schema(name = "status", description = "状态 0启用 1禁用")
	Integer status;

}
