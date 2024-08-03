package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.validate.AddGroup;
import com.ldapauth.validate.EditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: orangeBabu
 * @time: 10/7/2024 PM2:23
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdsListDto {
    @Valid
    @NotEmpty(message = "入参ID集合不能为空")
    List<Long> ids;
}
