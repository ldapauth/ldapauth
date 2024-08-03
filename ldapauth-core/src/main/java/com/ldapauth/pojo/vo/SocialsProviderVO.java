package com.ldapauth.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Schema(name = "SocialsProviderVO", description = "第三方列表")
@EqualsAndHashCode(callSuper = false)
@Data
public class SocialsProviderVO {

	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "icon", description = "图标")
	String icon;

	@Schema(name = "type", description = "类型")
	String type;

	@Schema(name = "name", description = "名称")
	String name;


}
