package com.ldapauth.pojo.dto;

import com.ldapauth.validate.QueryGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Schema(name = "GroupMemberQueryDTO", description = "分组成员查询接口")
@EqualsAndHashCode(callSuper = false)
@Data
public class GroupMemberQueryDTO extends PageQueryDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@NotNull(message = "分组编码不能为空", groups = { QueryGroup.class})
	@Schema(name = "groupId", description = "分组编码")
	Long groupId;

	@Schema(name = "username", description = "登录账号")
	String username;

	@Schema(name = "displayName", description = "显示名称")
	String displayName;

	@Schema(name = "mobile", description = "手机号码")
	String mobile;


}
