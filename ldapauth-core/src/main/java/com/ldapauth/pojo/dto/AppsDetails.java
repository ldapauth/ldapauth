package com.ldapauth.pojo.dto;

import com.ldapauth.pojo.entity.client.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/11  9:40
 */
@Schema(name = "IDPAppDetails", description = "应用及扩展信息对象")
@EqualsAndHashCode(callSuper = false)
@Data
public class AppsDetails<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public AppsDetails() {
        super();
    }

    private Client app;

    private T details;
}
