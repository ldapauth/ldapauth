package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotNull;


@Schema(name = "ChangeStatusDTO", description = "修改状态入参")
@EqualsAndHashCode(callSuper = false)
@Data
public class ChangeStatusDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@NotNull(message = "ID不能为空", groups = {AddGroup.class, EditGroup.class})
	@Schema(name = "id", description = "用户ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "status", description = "状态: 0-启用; 1-禁用")
	Integer status;


}
