package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/9  16:26
 */
@Data
public class AppsQueryDTO extends PageQueryDTO{
    @Schema(name = "instId", description = "instId")
    Long instId;

    @Schema(name = "appCode", description = "应用编码")
    String appCode;

    @Schema(name = "appName", description = "应用名称")
    String appName;

    @Schema(name = "protocol", description = "标签,0=oauth 1=saml  2=jwt 3=cas")
    Integer protocol;

    @Schema(name = "status", description = "状态 0正常 1禁用")
    Integer status;
}
