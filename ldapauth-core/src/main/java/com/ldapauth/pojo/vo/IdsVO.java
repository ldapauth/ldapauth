package com.ldapauth.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Schema(name = "IdsVO", description = "通用IDS返回对象")
@EqualsAndHashCode(callSuper = false)
@Data
public class IdsVO {

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Schema(name = "ids", description = "IDS集合")
	List<Long> ids;


}
