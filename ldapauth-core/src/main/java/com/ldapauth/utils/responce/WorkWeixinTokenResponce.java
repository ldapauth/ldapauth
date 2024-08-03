package com.ldapauth.utils.responce;

import lombok.Data;

@Data
public class WorkWeixinTokenResponce {

    private String errcode;
    private String errmsg;
    private String access_token;
    private Integer expires_in;

}
