package com.ldapauth.persistence.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.persistence.service.AppsService;
import com.ldapauth.persistence.service.GroupResourceService;
import com.ldapauth.persistence.service.PermissionAuthenticationService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.entity.Resource;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.entity.apps.Apps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/23  11:24
 */

@Slf4j
@Service
public class PermissionAuthenticationServiceImpl implements PermissionAuthenticationService {

    @Autowired
    CacheService cacheService;

    @Autowired
    GroupResourceService groupResourceService;

    @Autowired
    AppsService appsService;

    @Autowired
    @Lazy
    UserInfoService userInfoService;

    @Value("${server.servlet.context-path:/ldap-api}")
    String contextPath;

    AntPathMatcher matcher = new AntPathMatcher();

    /**
     * 白名单
     */
    private final static List<String> whiPath = Stream.of("/users/currentUser","/apps/myApps","/users/getRouters").collect(Collectors.toList());;


    @Override
    public boolean authApi(Long userId, String path) {
        //移除访问上下文
        path = path.replace(contextPath,"");
        if (whiPath.contains(path)) {
            return true;
        }
        List<String> apis = getResApiList(userId);
        for (String api : apis) {
            if(matcher.match(api,path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean authApp(Long userId, Long appId) {
        List<Long> appIds = getResAppsList(userId);
        if (appIds.contains(appId)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean reloadApi() {
        List<UserInfo> list = userInfoService.list();
        for (UserInfo userInfo : list) {
            String key = ConstsCacheData.RES_MENU_USER_ID + userInfo.getId();
            cacheService.deleteObject(key);
        }
        return true;
    }

    @Override
    public boolean reloadApp() {
        List<UserInfo> list = userInfoService.list();
        for (UserInfo userInfo : list) {
            String key = ConstsCacheData.RES_APPS_USER_ID + userInfo.getId();
            cacheService.deleteObject(key);
        }
        return true;
    }

    /**
     * 获取用户缓存资源URI列表
     * @return
     */
    private List<String> getResApiList(Long userId){
        String key = ConstsCacheData.RES_MENU_USER_ID + userId;
        List<String> res = cacheService.getCacheObject(key);
        if (CollectionUtil.isEmpty(res)) {
            //从数据库读取
            List<Resource> resourceList = groupResourceService.getResourceByMemberId(userId);
            if (CollectionUtil.isNotEmpty(resourceList)) {
                //提取所有api类型的资源
                resourceList = resourceList.stream().filter(resource -> resource.getClassify().equalsIgnoreCase("api")).collect(Collectors.toList());
                //提取所有权限标识
                res = resourceList.stream().map(Resource::getPermission).collect(Collectors.toList());
                //设置用户资源12小时
                cacheService.setCacheObject(key,res,12, TimeUnit.HOURS);
            }
        }
        if (CollectionUtil.isEmpty(res)) {
            res = new ArrayList<>();
        }
        return res;
    }

    /**
     * 获取用户缓存资源URI列表
     * @return
     */
    private List<Long> getResAppsList(Long userId){
        String key = ConstsCacheData.RES_APPS_USER_ID + userId;
        List<Long> res = cacheService.getCacheObject(key);
        if (CollectionUtil.isEmpty(res)) {
            //从数据库读取
            List<Apps> apps = appsService.myApps(userId);
            if (CollectionUtil.isNotEmpty(apps)) {
                //提取所有应用ID标识
                res = apps.stream().map(Apps::getId).collect(Collectors.toList());
                //设置用户资源12小时
                cacheService.setCacheObject(key,res,12, TimeUnit.HOURS);
            }
        }
        if (CollectionUtil.isEmpty(res)) {
            res = new ArrayList<>();
        }
        return res;
    }

}
