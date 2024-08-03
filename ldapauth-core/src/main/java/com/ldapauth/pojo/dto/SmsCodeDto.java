package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @description:
 * @author: orangeBabu
 * @time: 16/7/2024 PM2:44
 */

@Schema(name="SmsCodeDto",description ="发送验证码对象" )
@Data
public class SmsCodeDto {

    @NotEmpty(message = "手机号不能为空")
    String mobile;

    @NotEmpty(message = "口令类型不能为空")
    String otpType;
}
