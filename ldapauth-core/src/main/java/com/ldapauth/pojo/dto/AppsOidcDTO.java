package com.ldapauth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/11  16:23
 */
@Data
@Schema(name = "AppsOAuthDTO", description = "OAuthDTO对象")
public class AppsOidcDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    private AppsDTO app;

    @Valid
    private AppsOidcDetailsDTO details;
}
