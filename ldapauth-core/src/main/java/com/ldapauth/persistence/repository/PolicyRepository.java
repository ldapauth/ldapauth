package com.ldapauth.persistence.repository;

import java.util.ArrayList;
import com.ldapauth.pojo.entity.PolicyLogin;
import com.ldapauth.pojo.entity.PolicyPassword;
import com.ldapauth.persistence.service.PolicyLoginService;
import com.ldapauth.persistence.service.PolicyPasswordService;
import org.passay.Rule;

/**
 * 策略类
 */
public class PolicyRepository {

    //Dictionary topWeakPassword Source
    public static final String topWeakPasswordPropertySource      =
            "classpath:/top_weak_password.txt";

    protected PolicyPasswordService policyPasswordService;
    protected PolicyLoginService policyLoginService;
    ArrayList <Rule> passwordPolicyRuleList;


    public PolicyRepository(PolicyLoginService policyLoginService,PolicyPasswordService policyPasswordService) {
        this.policyLoginService = policyLoginService;
        this.policyPasswordService = policyPasswordService;
    }


    /**
     * 修改用户锁定状态
     * @param userId
     * @param lockStatus
     */
    public void updateLockStatus(Long userId,int lockStatus){
        policyLoginService.updateLockStatus(userId,lockStatus);
    }
    /**
     * 重置用户锁定状态和次数
     * @param userId 用户标识
     * @param lockStatus 锁定状态
     * @param badPasswordCount 次数
     */
    public void resetAttempts(Long userId,int lockStatus,int badPasswordCount){
        policyLoginService.resetAttempts(userId,lockStatus,badPasswordCount);
    }
    /**
     * 设置用户密码错误次数
     * @param userId 用户标识
     * @param badPasswordCount 次数
     */
    public void setBadPasswordCount(Long userId,int badPasswordCount){
        policyLoginService.setBadPasswordCount(userId,badPasswordCount);
    }

    /**
     * 获取登录策略
     * @return
     */
    public PolicyLogin getPolicyLogin() {
       return policyLoginService.getCache();
    }
    /**
     * 获取密码策略
     * @return
     */
    public PolicyPassword getPasswordPolicy() {
        return policyPasswordService.getCache();
    }

    public ArrayList<Rule> getPasswordPolicyRuleList() {
        getPasswordPolicy();
        return passwordPolicyRuleList;
    }
}
