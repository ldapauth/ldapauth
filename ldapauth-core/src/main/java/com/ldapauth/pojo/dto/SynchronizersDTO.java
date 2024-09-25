package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Schema(name = "SynchronizersDTO", description = "新增或者修改参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class SynchronizersDTO {


	@NotNull(message = "ID不能为空", groups = { EditGroup.class})
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "classify", description = "类型")
	String classify;

	@Schema(name = "cron", description = "任务执行表达式")
	String cron;

	@Schema(name = "baseApi", description = "链接地址，http://或者ldap://")
	String baseApi;

	@Schema(name = "baseDomain", description = "域名")
	String baseDomain;

	@Schema(name = "baseDn", description = "基本DN")
	String baseDn;

	@Schema(name = "clientId", description = "客户端ID，有可能是LDAP账号")
	String clientId;

	@Schema(name = "clientSecret", description = "客户端秘钥，有可能是LDAP密码")
	String clientSecret;


	@Schema(name = "userClientSecret", description = "客户端通讯录秘钥，主要用于企业微信")
	String userClientSecret;

	@Schema(name = "rootId", description = "根节点ID")
	String rootId;

	@Schema(name = "orgFilter", description = "组织过滤条件")
	String orgFilter;

	@Schema(name = "userFilter", description = "用户过滤条件")
	String userFilter;

	@Schema(name = "status", description = "状态: 0-启用; 1-禁用")
	Integer status;

	@Schema(name = "description", description = "备注")
	String description;

	@Schema(name = "userDn", description = "用户所在DN")
	String userDn;

	@Schema(name = "groupDn", description = "组所在DN")
	String groupDn;

	@Schema(name = "groupFilter", description = "组过滤条件")
	String groupFilter;

	@Schema(name = "openssl", description = "openssl")
	boolean openssl;

	@Schema(name = "sslFileId", description = "上传证书ID")
	Long sslFileId;

}
