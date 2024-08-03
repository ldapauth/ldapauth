package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.entity.PolicyLogin;

public interface PolicyLoginService extends IService<PolicyLogin> {

    PolicyLogin get();

    PolicyLogin getCache();


    /**
     * 修改用户锁定状态
     * @param userId
     * @param lockStatus
     */
    boolean updateLockStatus(Long userId,int lockStatus);

    /**
     * 重置用户锁定状态和次数
     * @param userId 用户标识
     * @param lockStatus 锁定状态
     * @param badPasswordCount 次数
     */
    boolean resetAttempts(Long userId,int lockStatus,int badPasswordCount);
    /**
     * 设置用户密码错误次数
     * @param userId 用户标识
     * @param badPasswordCount 次数
     */
    boolean setBadPasswordCount(Long userId,int badPasswordCount);

}
