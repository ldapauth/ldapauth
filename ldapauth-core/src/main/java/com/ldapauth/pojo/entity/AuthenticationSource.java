package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@TableName(value = "lda_authentication_source")
@Schema(name="AuthenticationSource",description ="认证源" )
@Data
public class AuthenticationSource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(name = "id",description = "认证源标识")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;

    @Schema(name = "asIcon",description = "认证源图标")
    String asIcon;

    @Schema(name = "asCode",description = "认证源编码")
    String asCode;

    @Schema(name = "asName",description = "认证源名称")
    String asName;

    @Schema(name = "asType",description = "认证类型：1=系统认证 2=LDAP 3=AD 4=第三方，第三方一般为开放平台，如微信、支付宝等")
    Integer asType;

    @Schema(name = "isShow",description = "登录页是否显示图标：0=不显示 1=显示")
    Integer isShow;

    @Schema(name = "isAllowFactor",description = "是否允许二次认证和双因素认证：0=不允许 1=允许")
    Integer isAllowFactor;

    @Schema(name = "scopes",description = "作用域，多个英文(,)隔开，inst=租户端、console=平台端,mobile=移动端")
    String scopes;

    @Schema(name = "asData",description = "认证源配置数据对象json格式")
    String asData;

    @Schema(name = "description", description = "描述")
    String description;

    @Schema(name = "status", description = "状态: 0-启用; 1-禁用")
    Integer status;

}
