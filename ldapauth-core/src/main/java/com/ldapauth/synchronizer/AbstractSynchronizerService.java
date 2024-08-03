package com.ldapauth.synchronizer;

import com.alibaba.fastjson.JSONObject;
import com.ldapauth.cache.CacheService;
import com.ldapauth.persistence.service.*;
import com.ldapauth.pojo.entity.*;
import com.ldapauth.utils.PasswordGenerator;
import com.ldapauth.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractSynchronizerService {

    protected Synchronizers synchronizer;
    SynchronizersLogsService synchronizersLogsService;
    CacheService cacheService;
    UserInfoService userInfoService;
    OrganizationService organizationService;

    GroupService groupService;

    public Synchronizers getSynchronizer() {
        return synchronizer;
    }

    /**
     * 插入历史记录
     */
    public void historyLogs(Object data,Long trackingUniqueId,Integer syncType, String syncActionType,Integer syncResultStatus,String syncMessage) {
        if (synchronizersLogsService == null) {
            synchronizersLogsService = SpringUtils.getBean(SynchronizersLogsService.class);
        }
        SynchronizersLogs logs = new SynchronizersLogs();
        logs.setTrackingUniqueId(trackingUniqueId);
        logs.setSyncActionType(syncActionType);
        logs.setSyncResultStatus(syncResultStatus);
        logs.setSyncMessage(syncMessage);
        logs.setCreateBy(0l);
        logs.setCreateTime(new Date());
        String fdata = JSONObject.toJSONString(data);
        //截取长度防止报错
        if (fdata.length() > 4096) {
            fdata = fdata.substring(0,4000)+" substring more ....";
        }
        logs.setSyncData(fdata);
        logs.setSyncType(syncType);
        logs.setSynchronizerId(synchronizer.getId());
        synchronizersLogsService.save(logs);
    }


    public void setSynchronizer(Synchronizers synchronizer) {
        this.synchronizer = synchronizer;
    }


    /**
     * 获取组织对象
     * @param openDepartmentId
     * @param objectFrom 来源
     * @return
     */
    public Long getOrgIdByOpenDepartmentId(Long trackingUniqueId,String objectFrom,String openDepartmentId) {
        String key = "sync:org:" +objectFrom+ trackingUniqueId + openDepartmentId;
        if (organizationService == null) {
            organizationService = SpringUtils.getBean(OrganizationService.class);
        }
        if (cacheService == null) {
            cacheService = SpringUtils.getBean(CacheService.class);
        }
        Long id = cacheService.getCacheObject(key);
        if (Objects.isNull(id)) {
            Organization organization = organizationService.getOrganizationByOpenDepartmentId(objectFrom,openDepartmentId);
            if (Objects.nonNull(organization)) {
                cacheService.setCacheObject(key, organization.getId(),30, TimeUnit.MINUTES);
                return organization.getId();
            }
            return 0L;
        }
        return id;
    }


    /**
     * 获取组织对象
     * @param ldapId
     * @return
     */
    public Long getOrgIdByLdapId(Long trackingUniqueId,String ldapId) {
        String key = "sync:org:" + trackingUniqueId + ldapId;
        if (organizationService == null) {
            organizationService = SpringUtils.getBean(OrganizationService.class);
        }
        if (cacheService == null) {
            cacheService = SpringUtils.getBean(CacheService.class);
        }
        Long id = cacheService.getCacheObject(key);
        if (Objects.isNull(id)) {
            Organization organization = organizationService.getOrganizationByLdapId(ldapId);
            if (Objects.nonNull(organization)) {
                cacheService.setCacheObject(key, organization.getId(),30, TimeUnit.MINUTES);
                return organization.getId();
            }
            return 0L;
        }
        return id;
    }

    /**
     * 获取用户对象
     * @param openId
     * @param objectFrom 来源
     * @return
     */
    public Long getUserIdByOpenDepartmentId(Long trackingUniqueId,String objectFrom,String openId){
        if (cacheService == null) {
            cacheService = SpringUtils.getBean(CacheService.class);
        }
        if (userInfoService == null) {
            userInfoService = SpringUtils.getBean(UserInfoService.class);
        }
        String key = "sync:user:"+objectFrom+trackingUniqueId+openId;
        Long id = cacheService.getCacheObject(key);
        if (Objects.isNull(id)) {
            UserInfo user = userInfoService.getUserByOpenId(objectFrom,openId);
            if (Objects.nonNull(user)) {
                cacheService.setCacheObject(key, user.getId(),30, TimeUnit.MINUTES);
                return user.getId();
            }
        }
        return 0L;
    }


    /**
     * 获取用户对象
     * @param ldapId
     * @return
     */
    public Long getUserIdByLdapId(Long trackingUniqueId,String ldapId){
        if (cacheService == null) {
            cacheService = SpringUtils.getBean(CacheService.class);
        }
        if (userInfoService == null) {
            userInfoService = SpringUtils.getBean(UserInfoService.class);
        }
        String key = "sync:user:"+trackingUniqueId+ldapId;
        Long id = cacheService.getCacheObject(key);
        if (Objects.isNull(id)) {
            UserInfo user = userInfoService.getUserByLdapId(ldapId);
            if (Objects.nonNull(user)) {
                cacheService.setCacheObject(key, user.getId(),30, TimeUnit.MINUTES);
                return user.getId();
            }
        }
        return 0L;
    }

    /**
     * 获取组openId
     * @param ldapId
     * @return
     */
    public Long getGroupIdByLdapId(Long trackingUniqueId,String ldapId){
        if (cacheService == null) {
            cacheService = SpringUtils.getBean(CacheService.class);
        }
        if (groupService == null) {
            groupService = SpringUtils.getBean(GroupService.class);
        }
        String key = "sync:group:"+trackingUniqueId+ldapId;
        Long id = cacheService.getCacheObject(key);
        if (Objects.isNull(id)) {
            Group group = groupService.getGroupIdByLdapId(ldapId);
            if (Objects.nonNull(group)) {
                cacheService.setCacheObject(key, group.getId(),30, TimeUnit.MINUTES);
                return group.getId();
            }
        }
        return 0L;
    }



    /**
     * 处理手机号码，为空或者带区号
     * @param mobile
     * @return
     */
    public String handLeMobile(String mobile){
        if (StringUtils.isEmpty(mobile)) {
            return mobile;
        } else if (mobile.length() <= 11) {
            return mobile;
        } else if (mobile.length() > 11) {
            //保留后十一位，处理区号的问题，如+861922211231,+8618661251123等
            int endIndex = mobile.length();
            int startIndex = endIndex - 11;
            return mobile.substring(startIndex,endIndex);
        }
        return mobile;
    }

    /**
     * 根据策略随机生成密码
     * @return
     */
    public String randomPassword(PolicyPasswordService policyPasswordService){
        PolicyPassword policyPassword = policyPasswordService.getCache();
        if (Objects.isNull(policyPassword)) {
            policyPassword = new PolicyPassword();
            policyPassword.setMinLength(8);
            policyPassword.setMaxLength(20);
            policyPassword.setIsLowerCase(1);
            policyPassword.setIsSpecial(1);
            policyPassword.setIsDigit(1);
        }
        //根据密码策略生成密码
        String randomPassword =
                PasswordGenerator.generatePassword(policyPassword.getMinLength(), policyPassword.getMaxLength(),
                        policyPassword.getIsLowerCase(),
                        policyPassword.getIsSpecial(),
                        policyPassword.getIsDigit());
        return randomPassword;
    }

}
