package com.ldapauth.pojo.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Schema(name="SystemsDTO",description ="系统配置修改入参对象" )
@Data
public class SystemsDTO  {

    @NotNull(message = "ID不能为空",groups = EditGroup.class)
    @Schema(name = "id",description = "ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;

    @NotEmpty(message = "系统logo不能为空",groups = EditGroup.class)
    @Schema(name = "logo", description = "系统logo")
    String logo;

    @NotEmpty(message = "系统名称不能为空",groups = EditGroup.class)
    @Schema(name = "title", description = "系统名称")
    String title;

    @NotEmpty(message = "版权不能为空",groups = EditGroup.class)
    @Schema(name = "copyright", description = "版权")
    String copyright;

}
