package com.ldapauth.domain;

import lombok.Data;

import java.util.List;

@Data
public class WorkweixinUser {

    String userid;

    String name;

    List<String> department;

    /**
     * 通讯录安全机制，无法获取
     */
    String mobile;

    /**
     * 通讯录安全机制，无法获取
     */
    String email;

    String gender;

    String position;

    /**
     * 通讯录安全机制，无法获取
     */
    String avatar;

    /**
     * 座机
     */
    String telephone;

    /**
     *  激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业。
     */
    int status;




}
