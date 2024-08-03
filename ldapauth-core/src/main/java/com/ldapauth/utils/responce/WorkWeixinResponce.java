package com.ldapauth.utils.responce;

import lombok.Data;

@Data
public class WorkWeixinResponce {

    String errmsg;
    /**
     * errcode
     */
    Integer errcode;

    Integer id;

    public static WorkWeixinResponce error(String message){
        WorkWeixinResponce responce = new WorkWeixinResponce();
        responce.setErrcode(500);
        responce.setErrmsg(message);
        return responce;
    }
}
