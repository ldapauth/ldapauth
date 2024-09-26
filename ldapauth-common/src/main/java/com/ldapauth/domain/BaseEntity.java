package com.ldapauth.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity基类
 *
 * @author Lion Li
 */

@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public BaseEntity(){

    }
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

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(name = "updateBy",description = "更新人")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Schema(name = "updateTime",description = "更新时间")
    Date updateTime;

    @TableField(exist = false)
    @Schema(name = "sync",description = "是否同步-默认否")
    boolean sync = false;


}
