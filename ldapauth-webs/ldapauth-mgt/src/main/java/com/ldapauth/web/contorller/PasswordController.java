package com.ldapauth.web.contorller;

import com.ldapauth.persistence.service.SmsService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.dto.MobileValidateDto;
import com.ldapauth.pojo.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: orangeBabu
 * @time: 17/7/2024 PM2:37
 */

@Tag(
        name = "密码设置",
        description = "提供找回密码相关服务"
)
@RequestMapping("/password")
@RestController
@Slf4j
public class PasswordController {

    @Autowired
    UserInfoService userInfoService;

    @Operation(
            summary = "初步验证", description = "返回结果",
            parameters = {@Parameter(name = "userPassword", description = "验证信息")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "成功"
    )
    @GetMapping("/validate")
    public Result<String> validate(@Validated @ParameterObject MobileValidateDto dto) {
        return userInfoService.validate(dto);
    }


}
