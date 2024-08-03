package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = false)
@Data
@TableName(value = "lda_group_member")
@Schema(name = "GroupMember", description = "组成员关系表")
public class GroupMember extends BaseEntity {

	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "groupId", description = "分组编码")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long groupId;

	@Schema(name = "memberId", description = "成员编码")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long memberId;

}
