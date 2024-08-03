package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@TableName("lda_group")
@Schema(name = "Group", description = "分组信息")
@Data
public class Group extends BaseEntity {

	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "objectFrom", description = "对象来源 ldap=ldap 或者 system=本地")
	String objectFrom;

	@Schema(name = "name", description = "组名称")
	String name;

	@Schema(name = "description", description = "描述")
	String description;

	@TableField(fill = FieldFill.INSERT)
	@Schema(name = "status", description = "状态 0启用 1禁用")
	Integer status;


	@Schema(name = "ldapDn", description = "ldap 路径（ldapdn）")
	String ldapDn;

	@Schema(name = "openId", description = "对外开放ID")
	String openId;

	@Schema(name = "ldapId", description = "LDAP唯一标识（entryUUID）")
	String ldapId;
}
