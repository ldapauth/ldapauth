package com.ldapauth.utils.responce;

import com.ldapauth.domain.WorkweixinUser;
import lombok.Data;

import java.util.List;

@Data
public class WorkWeixinUsersResponce {

    String errmsg;
    /**
     * errcode
     */
    int errcode;

    List<WorkweixinUser> userlist;

}
