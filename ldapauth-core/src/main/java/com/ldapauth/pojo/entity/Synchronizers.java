package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: 24096
 * @time: 2/7/2023 AM10:43
 */


@TableName(value = "lda_synchronizers")
@Schema(name = "Synchronizers", description = "同步器表")
@EqualsAndHashCode(callSuper = false)
@Data
public class Synchronizers extends BaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "标识")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "masterType", description = "主数据: 0-是; 1-否")
	Integer masterType;

	@Schema(name = "classify", description = "类型 openldap=openldap ad-AD feishu-飞书 dingding-钉钉 workweixin-企业微信")
	String classify;

	@Schema(name = "cron", description = "任务执行表达式")
	String cron;

	@Schema(name = "baseApi", description = "链接地址，http://或者ldap://")
	String baseApi;

	@Schema(name = "baseDn", description = "基本DN")
	String baseDn;

	@Schema(name = "userDn", description = "用户所在DN")
	String userDn;

	@Schema(name = "groupDn", description = "组所在DN")
	String groupDn;

	@Schema(name = "groupFilter", description = "组过滤条件")
	String groupFilter;


	@Schema(name = "baseDomain", description = "域名")
	String baseDomain;

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


}
