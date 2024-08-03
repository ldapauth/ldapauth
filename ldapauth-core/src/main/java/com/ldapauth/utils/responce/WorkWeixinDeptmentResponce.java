package com.ldapauth.utils.responce;

import com.ldapauth.domain.WorkweixinDeptment;
import lombok.Data;

import java.util.List;

@Data
public class WorkWeixinDeptmentResponce {

    String errmsg;
    /**
     * errcode
     */
    int errcode;

    List<WorkweixinDeptment> department;

}
