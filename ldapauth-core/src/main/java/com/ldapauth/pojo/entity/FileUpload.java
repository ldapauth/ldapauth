package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.util.Date;

/**
 * @description:
 * @author: orangeBabu
 * @time: 4/7/2024 PM2:39
 */


@TableName(value = "lda_file_upload")
@Schema(name = "FileUpload", description = "文件上传表")
@EqualsAndHashCode(callSuper = false)
@Data
public class FileUpload {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(name = "id", description = "文件标识")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;

    @Schema(name = "fileName", description = "文件名称")
    String fileName;

    @Schema(name = "uploaded", description = "blob文件")
    byte[] uploaded;

    @Schema(name = "contentSize", description = "文件大小")
    Long contentSize;

    @Schema(name = "contentType", description = "文件类型")
    String contentType;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(name = "createBy",description = "创建人")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Schema(name = "createTime",description = "创建时间")
    Date createTime;
}
