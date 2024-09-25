package com.ldapauth.pojo.entity.apps.details;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "lda_client_apps_cas_details")
@Schema(name = "ClientAppsCASDetails", description = "CAS扩展表")
@EqualsAndHashCode(callSuper = false)
@Data
public class ClientAppsCASDetails extends BaseEntity {

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

	@Schema(name = "serverNames", description = "CAS客户端service")
	String serverNames;

	@Schema(name = "ipWhiteIds", description = "IP白名单集合")
	String ipWhiteIds;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Schema(name = "ticketValidity", description = "ticket有效时间(单位秒)")
	Long ticketValidity;

	@Schema(name = "status", description = "状态 0正常 1禁用")
	Integer status;


}
