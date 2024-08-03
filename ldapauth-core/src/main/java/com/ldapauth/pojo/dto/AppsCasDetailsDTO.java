package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/9  16:17
 */
@Data
@EqualsAndHashCode
public class AppsCasDetailsDTO {
    @NotNull(message = "主建不能为空", groups = {EditGroup.class})
    @Schema(name = "appId", description = "应用ID")
    Long appId;

    @NotBlank(message = "响应主体不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "subject", description = "响应主体-username/mobile/email等")
    String subject;

    @NotBlank(message = "serverNames不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "serverNames", description = "serverNames")
    String serverNames;

    @Schema(name = "ipWhiteIds", description = "IP白名单集合")
    String ipWhiteIds;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(name = "ticketValidity", description = "ticket有效时间(单位秒)")
    Long ticketValidity;
}
