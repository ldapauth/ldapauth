package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @description:
 * @author: orangeBabu
 * @time: 17/7/2024 PM5:08
 */

@Data
public class PwdSetDto {

    @NotEmpty(message = "密码不能为空")
    @Schema(name = "password", description = "密码")
    String password;

    @NotEmpty(message = "手机号码不能为空")
    @Schema(name = "mobile", description = "手机号码")
    String mobile;

    @NotEmpty(message = "临时缓存ID不能为空")
    @Schema(name = "tempId", description = "临时缓存ID")
    String tempId;
}
