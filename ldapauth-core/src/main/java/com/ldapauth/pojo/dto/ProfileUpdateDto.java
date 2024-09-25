package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @description:
 * @author: orangeBabu
 * @time: 18/7/2024 PM4:34
 */

@Schema(name = "ProfileUpdateDto", description = "个人资料更新对象")
@Data
public class ProfileUpdateDto {

    @Schema(name = "username",description = "登录账号")
    String username;

    @NotEmpty(message = "姓名不能为空")
    @Schema(name = "displayName", description = "姓名")
    String displayName;

    @Schema(name = "nickName", description = "昵称")
    String nickName;

    @Schema(name = "mobile", description = "手机号码")
    String mobile;

    @Schema(name = "email", description = "邮箱")
    String email;

    @Schema(name = "gender", description = "性别:0-其他;1-男;2-女")
    Integer gender;

    @Schema(name = "birthDate", description = "出生日期")
    @JsonFormat(
            pattern = "yyyy-MM-dd",
            timezone = "GMT+8"
    )
    Date birthDate;
}
