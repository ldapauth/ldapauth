package com.ldapauth.web.contorller;

import com.ldapauth.persistence.service.SmsService;
import com.ldapauth.pojo.dto.SmsAliyunDto;
import com.ldapauth.pojo.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: orangeBabu
 * @time: 12/7/2024 PM2:24
 */

@Tag(
        name = "短信发送",
        description = "提供短信发送功能"
)
@RestController
@RequestMapping(value = "sms")
@Slf4j
public class SmsController {

    @Autowired
    SmsService smsService;

    @Operation(
            summary = "发送短信", description = "返回结果",
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "成功"
    )
    @PostMapping(value = "/send")
    public Result send(@RequestBody SmsAliyunDto model) {
        return smsService.send(model);
    }
}
