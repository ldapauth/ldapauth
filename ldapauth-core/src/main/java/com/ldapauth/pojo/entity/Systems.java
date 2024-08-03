package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@TableName(value = "lda_system")
@Schema(name="Systems",description ="系统配置")
@Data
public class Systems extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(name = "id",description = "ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;

    @Schema(name = "logo", description = "系统logo")
    String logo;

    @Schema(name = "title", description = "系统名称")
    String title;

    @Schema(name = "copyright", description = "版权")
    String copyright;

}
