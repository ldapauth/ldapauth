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


@TableName(value = "lda_group_resources")
@Schema(name = "GroupResource", description = "组-资源表")
@EqualsAndHashCode(callSuper = false)
@Data
public class GroupResource extends BaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "groupId", description = "分组编码")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long groupId;

	@Schema(name = "resourceId", description = "资源编码")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long resourceId;

}
