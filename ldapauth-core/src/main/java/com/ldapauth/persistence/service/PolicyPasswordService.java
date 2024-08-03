package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.entity.PolicyPassword;
import com.ldapauth.pojo.vo.Result;

public interface PolicyPasswordService extends IService<PolicyPassword> {

    PolicyPassword get();

    PolicyPassword getCache();

    /**
     * @Description: 判断密码是否符合策略
     * @Param: [password]
     */
    Result<String> validatePassword(String password);

}
