package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description:
 * @author: 24096
 * @time: 29/6/2023 PM6:28
 */

@TableName(value = "lda_organization")
@Schema(name = "Organization", description = "组织机构信息")
@Data
public class Organization extends BaseEntity {

	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "组织ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "objectFrom", description = "对象来源 ldap=ldap 或者 system=本地")
	String objectFrom;

	@Schema(name = "ldapDn", description = "ldap 路径（ldapdn）")
	String ldapDn;

	@Schema(name = "orgName", description = "名称")
	String orgName;

	@Schema(name = "classify", description = "分类 department=部门 或者 organization=组织")
	String classify;

	@Schema(name = "parentId", description = "父级ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long parentId;

	@Schema(name = "parentName", description = "父级名称")
	String parentName;

	@Schema(name = "namePath", description = "名称路径")
	String namePath;

	@Schema(name = "idPath", description = "ID路径")
	String idPath;

	@Schema(name = "status", description = "状态")
	Integer status;

	@Schema(name = "sortIndex", description = "排序")
	Integer sortIndex;

	@Schema(name = "openDepartmentId", description = "对外开放ID")
	String openDepartmentId;


	@Schema(name = "ldapId", description = "LDAP唯一标识（entryUUID）")
	String ldapId;
}
