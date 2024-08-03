package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/23  11:09
 */
@Data
public class AppLoginLogQueryDTO extends PageQueryDTO{

    @Schema(description = "用户显示名称")
    String displayName;

    @Schema(description = "应用名称")
    String appName;

    @Schema(description = "ip地址")
    String ipAddr;

    @Schema(name = "startDate", description = "开始时间")
    private String startDate;

    @Schema(name = "endDate", description = "结束时间")
    private String endDate;
}
