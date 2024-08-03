package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @description:
 * @author: orangeBabu
 * @time: 17/7/2024 PM2:39
 */

@Schema(name="MobileValidateDto",description ="短信验证码验证对象" )
@Data
public class MobileValidateDto {

    @NotEmpty(message = "手机号码不能为空")
    @Schema(name = "mobile", description = "手机号码")
    private String mobile;

    @NotEmpty(message = "短信验证码不能为空")
    @Schema(name = "mobileOtp", description = "短信验证码")
    String mobileOtp;

    @NotEmpty(message = "验证码类型不能为空")
    @Schema(name = "otpType", description = "验证码类型")
    String otpType;
}
