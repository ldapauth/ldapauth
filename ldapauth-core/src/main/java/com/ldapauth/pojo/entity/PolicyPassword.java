package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "lda_policy_password")
@Schema(name="PolicyPassword",description ="密码策略")
@Data
public class PolicyPassword extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(name = "id",description = "ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;

    @Schema(name = "minLength",description = "最小长度")
    Integer minLength;

    @Schema(name = "maxLength",description = "最大长度")
    Integer maxLength;

    @Schema(name = "expirationDays",description = "过期时间(天)")
    Integer expirationDays;


    @Schema(name = "isDigit",description = "数字")
    Integer isDigit;

    @Schema(name = "isSpecial",description = "特殊字符")
    Integer isSpecial;

    @Schema(name = "isLowerCase",description = "小写字母")
    Integer isLowerCase;

    @TableField(exist = false)  // 默认为true
    @Schema(name = "policyPrompt",description = "策略提示词")
    String policyPrompt;

    @JsonFormat
    @TableField(exist = false)
    @Schema(name = "policyPrompt",description = "正则表达式")
    String policyRegex;

}
