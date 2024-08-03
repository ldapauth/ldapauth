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
@TableName("lda_socials_associate")
@Schema(name = "SocialsAssociate", description = "第三方关联用户表")
@Data
public class SocialsAssociate extends BaseEntity {

	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "icon", description = "系统用户ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long userId;

	@Schema(name = "socialsUserId", description = "第三方用户ID")
	String socialsUserId;

	@Schema(name = "socialsId", description = "第三方ID")
	Long socialsId;

	@Schema(name = "socialsAvatar", description = "第三方头像")
	String socialsAvatar;

	@Schema(name = "socialsNickName", description = "第三方昵称")
	String socialsNickName;

}
