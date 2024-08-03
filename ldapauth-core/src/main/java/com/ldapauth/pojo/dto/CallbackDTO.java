package com.ldapauth.pojo.dto;

import com.ldapauth.validate.QueryGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Schema(name = "CallbackDTO", description = "获取第三方用户入参DTO")
@EqualsAndHashCode(callSuper = false)
@Data
public class CallbackDTO {

    private static final long serialVersionUID = 1L;
    @NotNull(message = "第三方标记不能为空", groups = { QueryGroup.class})
    @Schema(name = "id", description = "第三方标记")
    Long id;

    @Schema(name = "code", description = "临时票据")
    String code;

    @Schema(name = "auth_code", description = "授权票据")
    String auth_code;

    @Schema(name = "state", description = "客户端标记")
    String state;
    @Schema(name = "authorization_code", description = "授权标记")
    String authorization_code;

    @Schema(name = "oauthVerifier", description = "认证验证")
    String oauthVerifier;

    @Schema(name = "oauthToken", description = "授权token")
    String oauthToken;
}
