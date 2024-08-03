package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/23  10:59
 */
@Data
public class LoginLogQueryDTO extends PageQueryDTO{

    @Schema(description = "用户显示名称")
    String displayName;

    @Schema(description = "登录方式")
    String loginType;

    @Schema(description = "提供商")
    String provider;

    @Schema(description = "ip地址")
    String ipAddr;

    @Schema(description = "状态 0成功 1失败")
    Integer status;

    @Schema(name = "startDate", description = "开始时间")
    String startDate;

    @Schema(name = "endDate", description = "结束时间")
    String endDate;
}
