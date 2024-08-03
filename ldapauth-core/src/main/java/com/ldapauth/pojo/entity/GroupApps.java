package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = false)
@Data
@TableName(value = "lda_group_apps")
@Schema(name = "GroupApps", description = "分组应用关系")
public class GroupApps extends BaseEntity {

	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "groupId", description = "分组编码")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long groupId;

	@Schema(name = "appId", description = "应用编码")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long appId;

}
