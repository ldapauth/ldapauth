package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Date;


@Schema(name = "GroupDTO", description = "新增或者修改参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class GroupDTO {

	@NotNull(message = "ID不能为空", groups = { EditGroup.class})
	@Schema(name = "id", description = "ID")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "objectFrom", description = "对象来源 ldap=ldap 或者 system=本地")
	String objectFrom;

	@Schema(name = "ldapDn", description = "ldap 路径（ldapdn）")
	String ldapDn;

	@NotNull(groups = {AddGroup.class, EditGroup.class},message = "组名称不能为空")
	@Schema(name = "name", description = "组名称")
	String name;

	@Schema(name = "description", description = "描述")
	String description;


	@NotNull(groups ={AddGroup.class, EditGroup.class},message = "状态不能为空")
	@Schema(name = "status", description = "状态: 0-启用; 1-禁用")
	Integer status;

}
