package com.ldapauth.pojo.dto;

import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/9  16:06
 */
@Data
@EqualsAndHashCode
public class AppsDTO {
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    @Schema(name = "id", description = "ID")
    Long id;

    @Schema(name = "appCode", description = "应用编码")
    @NotBlank(message = "应用编码不能为空",groups = {AddGroup.class, EditGroup.class})
    @Size(min = 1, max = 64, message = "应用编码长度必须介于1和64之间",groups = {AddGroup.class, EditGroup.class})
    String appCode;

    @Schema(name = "appName", description = "应用名称")
    @NotBlank(message = "应用名称不能为空",groups = {AddGroup.class, EditGroup.class})
    @Size(min = 1, max = 64, message = "应用名称长度必须介于1和64之间",groups = {AddGroup.class, EditGroup.class})
    String appName;

    @NotBlank(message = "应用图标不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(name = "icon", description = "图标")
    String icon;

    @Size(min = 1, max = 1024, message = "应用访问路径必须介于1和1024之间")
    @Schema(name = "path",description = "访问路径-鉴权路径")
    @NotBlank(message = "应用访问路径不能为空",groups = {AddGroup.class, EditGroup.class})
    String path;

    @Schema(name = "protocol", description = "标签,0=oauth 1=saml 2=oidc 3=jwt 4=cas")
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

    @Schema(name = "instId", description = "租户ID")
    Long instId;

    @Schema(name = "status", description = "状态 0正常 1禁用")
    Integer status;
}
