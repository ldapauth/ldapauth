package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/9  16:05
 */
@Data
@Schema(name = "AppCasDto", description = "CAS DTO对象")
public class AppsCasDTO {
    private static final long serialVersionUID = 1L;

    @Valid
    private AppsDTO app;

    @Valid
    private AppsCasDetailsDTO details;

}