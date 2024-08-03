package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description:
 * @author: orangeBabu
 * @time: 15/7/2024 AM11:33
 */

@Data
@Schema(name="SmsProviderQueryDto",description =" 查询对象" )
public class SmsProviderQueryDto extends PageQueryDTO{

    private static final long serialVersionUID = -6506607178415846655L;

    @Schema(name = "provider",description = "短信厂商")
    String provider;
}
