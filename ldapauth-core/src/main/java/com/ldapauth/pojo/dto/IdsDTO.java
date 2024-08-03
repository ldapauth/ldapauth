package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;


@Schema(name = "IdsDTO", description = "集合入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class IdsDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@NotNull(message = "源ID集合不能为空", groups = {AddGroup.class, EditGroup.class})
	@Schema(name = "ids", description = "源ID集合")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	List<Long> ids;

	@Schema(name = "targetIds", description = "目标ID集合")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	List<Long> targetIds;


}
