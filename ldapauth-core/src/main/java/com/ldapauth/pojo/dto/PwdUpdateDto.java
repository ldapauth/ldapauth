package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @description:
 * @author: orangeBabu
 * @time: 19/7/2024 AM9:49
 */

@Data
@Schema(name = "PwdUpdateDto", description = "密码更新对象")
public class PwdUpdateDto {

    @Schema(name = "username",description = "登录账号")
    String username;

    @NotEmpty(message = "原密码不能为空")
    @Schema(name = "oldPassword",description = "原密码")
    String oldPassword;

    @NotEmpty(message = "新密码不能为空")
    @Schema(name = "newPassword",description = "新密码")
    String newPassword;
}
