package com.ldapauth.pojo.entity.apps;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "lda_client_apps")
@Schema(name="ClientApps",description ="客户端应用主表")
@Data
public class ClientApps extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(name = "id", description = "ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;

    @Schema(name = "appCode", description = "应用编码")
    String appCode;

    @Schema(name = "appName", description = "应用名称")
    String appName;

    @Schema(name = "path",description = "访问路径-鉴权路径")
    String path;

    @Schema(name = "sysId", description = "系统ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long sysId;

    @Schema(name = "icon", description = "图标")
    String icon;

    @Schema(name = "protocol", description = "标签,0=oidc 1=saml 2=jwt 3=cas")
    Integer protocol;

    @Schema(name = "securityLevel", description = "安全级别，1~10，级别越高，安全越高")
    Integer securityLevel;

    @Schema(name = "deviceType", description = "设备类型，0=移动端 1=PC端 2=WEB端")
    Integer deviceType;

    @Schema(name = "homeUri", description = "应用访问地址")
    String homeUri;

    @Schema(name = "logoutUri", description = "应用退出地址")
    String logoutUri;

    @Schema(name = "description", description = "描述")
    String description;

    @Schema(name = "status", description = "状态 0正常 1禁用")
    Integer status;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @Schema(name = "clientId", description = "客户端ID")
    String clientId;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @Schema(name = "clientSecret", description = "客户端密钥")
    String clientSecret;

}
