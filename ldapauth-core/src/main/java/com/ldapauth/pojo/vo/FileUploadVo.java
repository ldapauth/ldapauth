package com.ldapauth.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: orangeBabu
 * @time: 4/7/2024 PM5:57
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadVo {
    @Schema(name = "id", description = "文件标识")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;
}
