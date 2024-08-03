package com.ldapauth.persistence.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.persistence.mapper.PolicyLoginMapper;
import com.ldapauth.persistence.service.PolicyLoginService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.entity.PolicyLogin;
import com.ldapauth.pojo.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PolicyLoginServicempl extends ServiceImpl<PolicyLoginMapper, PolicyLogin> implements PolicyLoginService {

    @Autowired
    CacheService cacheService;

    @Autowired
    UserInfoService userInfoService;


    @Override
    public PolicyLogin get() {
        List<PolicyLogin> list = super.list();
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        PolicyLogin policyLogin = new PolicyLogin();
        policyLogin.setId(1L);
        policyLogin.setLoginLockInterval(30);
        policyLogin.setIsCaptcha(1);
        policyLogin.setPasswordAttempts(3);
        policyLogin.setRefreshTokenValidity(18000);
        policyLogin.setAccessTokenValidity(1800);
        return policyLogin;
    }


    @Override
    public boolean saveOrUpdate(PolicyLogin entity) {
        boolean flag =  super.saveOrUpdate(entity);
        if(flag) {
            cacheService.deleteObject(ConstsCacheData.POLICY_LOGIN_KEY);
            getCache();
        }
        return flag;
    }

    @Override
    public PolicyLogin getCache() {
        PolicyLogin policy = cacheService.getCacheObject(ConstsCacheData.POLICY_LOGIN_KEY);
        if(Objects.isNull(policy)) {
            policy = get();
            cacheService.setCacheObject(ConstsCacheData.POLICY_LOGIN_KEY,policy);
        }
        return policy;
    }

    @Override
    public boolean updateLockStatus(Long userId, int lockStatus) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserInfo::getIsLocked,lockStatus);
        updateWrapper.eq(UserInfo::getId,userId);
        return userInfoService.update(updateWrapper);
    }

    @Override
    public boolean resetAttempts(Long userId, int lockStatus, int badPasswordCount) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserInfo::getBadPasswordCount,badPasswordCount);
        updateWrapper.set(UserInfo::getBadPasswordTime,new Date());
        updateWrapper.set(UserInfo::getIsLocked,lockStatus);
        updateWrapper.eq(UserInfo::getId,userId);
        return userInfoService.update(updateWrapper);
    }

    @Override
    public boolean setBadPasswordCount(Long userId, int badPasswordCount) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserInfo::getBadPasswordCount,badPasswordCount);
        updateWrapper.set(UserInfo::getBadPasswordTime,new Date());
        updateWrapper.eq(UserInfo::getId,userId);
        return userInfoService.update(updateWrapper);
    }
}
