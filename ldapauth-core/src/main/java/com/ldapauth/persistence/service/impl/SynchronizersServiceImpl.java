package com.ldapauth.persistence.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.SelfBuiltAppAccessTokenReq;
import com.lark.oapi.core.response.AppAccessTokenResp;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.constants.ConstsMasterType;
import com.ldapauth.constants.ConstsStatus;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.persistence.mapper.SynchronizersMapper;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.pojo.dto.ChangeStatusDTO;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.synchronizer.ISynchronizerService;
import com.ldapauth.util.LdapUtils;
import com.ldapauth.utils.SpringUtils;
import com.ldapauth.utils.WorkWeixinClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.directory.DirContext;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SynchronizersServiceImpl extends ServiceImpl<SynchronizersMapper, Synchronizers> implements SynchronizersService {

    @Override
    public List<Synchronizers> list() {
        return super.list();
    }

    ExecutorService executorService = Executors.newFixedThreadPool(200);

    @Autowired
    CacheService cacheService;


    @Override
    public boolean test(Synchronizers synchronizers) {
        boolean success = false;
        switch (synchronizers.getId().intValue()) {
            //OpenLdap
            case 1:
                success = testLdap(synchronizers);
                break;
            //钉钉
            case 2:
                success = testDingding(synchronizers);
                break;
            //飞书
            case 3:
                success = testFeishu(synchronizers);
                break;
            //企业微信
            case 4:
                success = testWorkWeixin(synchronizers);
                break;
            default:
                success =  false;
                break;
        }
        return success;
    }

    @Override
    public boolean sync(Long synchronizerId) {
        boolean success = true;
        Synchronizers synchronizers = super.getById(synchronizerId);
        if (Objects.isNull(synchronizers)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"任务不存在");
        }
        if (synchronizers.getStatus().intValue() == ConstsStatus.DATA_INACTIVE) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"任务已禁用");
        }
        ISynchronizerService synchronizerService;
        String key = "synchronizer:"+synchronizerId;
        String lock = cacheService.getCacheObject(key);
        if (Objects.nonNull(lock)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"任务触发频繁，请稍后再试.");
        }
        //设置锁
        //cacheService.setCacheObject(key,"lock");
        switch (synchronizerId.intValue()) {
            //OpenLdap
            case 1:
                synchronizerService = SpringUtils.getBean("ldapSynchronizerService");
                synchronizerService.setSynchronizer(synchronizers);
                //异步执行
                executorService.execute(() -> synchronizerService.sync());
                break;
            //钉钉
            case 2:
                synchronizerService = SpringUtils.getBean("dingtalkSynchronizerService");
                synchronizerService.setSynchronizer(synchronizers);
                //异步执行
                executorService.execute(() -> synchronizerService.sync());
                break;
            //飞书
            case 3:
                synchronizerService = SpringUtils.getBean("feishuSynchronizerService");
                synchronizerService.setSynchronizer(synchronizers);
                //异步执行
                executorService.execute(() -> synchronizerService.sync());
                break;
            //企业微信
            case 4:
                synchronizerService = SpringUtils.getBean("workweixinSynchronizerService");
                synchronizerService.setSynchronizer(synchronizers);
                //异步执行
                executorService.execute(() -> synchronizerService.sync());
                break;
            default:
                success = false;
                break;
        }
        return success;
    }

    @Override
    public Synchronizers LdapConfig() {
        Synchronizers synchronizers = cacheService.getCacheObject(ConstsCacheData.SYNC_LDAP_KEY);
        if(Objects.isNull(synchronizers)) {
            synchronizers = getById(1L);
            cacheService.setCacheObject(ConstsCacheData.SYNC_LDAP_KEY,synchronizers);
        }
        return synchronizers;
    }

    @PostConstruct
    private void init(){
        //初始化数据
        if (CollectionUtil.isEmpty(this.list())) {
            Synchronizers synchronizers = new Synchronizers();
            synchronizers.setId(1L);
            synchronizers.setClassify(ConstsSynchronizers.OPEN_LDAP);
            synchronizers.setStatus(ConstsStatus.DATA_INACTIVE);
            synchronizers.setMasterType(ConstsMasterType.YES);
            synchronizers.setCron("0 0 0/6 * * ?");
            synchronizers.setBaseApi("ldap://test.com");
            super.saveOrUpdate(synchronizers);

            synchronizers = new Synchronizers();
            synchronizers.setId(2L);
            synchronizers.setClassify(ConstsSynchronizers.DINGDING);
            synchronizers.setStatus(ConstsStatus.DATA_INACTIVE);
            synchronizers.setMasterType(ConstsMasterType.NO);
            synchronizers.setRootId("1");
            synchronizers.setCron("0 0 0/6 * * ?");
            synchronizers.setBaseApi("https://oapi.dingtalk.com");
            super.saveOrUpdate(synchronizers);

            synchronizers = new Synchronizers();
            synchronizers.setId(3L);
            synchronizers.setClassify(ConstsSynchronizers.FEISHU);
            synchronizers.setStatus(ConstsStatus.DATA_INACTIVE);
            synchronizers.setMasterType(ConstsMasterType.NO);
            synchronizers.setRootId("0");
            synchronizers.setCron("0 0 0/6 * * ?");
            synchronizers.setBaseApi("https://open.feishu.cn");
            super.saveOrUpdate(synchronizers);

            synchronizers = new Synchronizers();
            synchronizers.setId(4L);
            synchronizers.setClassify(ConstsSynchronizers.WORKWEIXIN);
            synchronizers.setStatus(ConstsStatus.DATA_INACTIVE);
            synchronizers.setMasterType(ConstsMasterType.NO);
            synchronizers.setRootId("1");
            synchronizers.setCron("0 0 0/6 * * ?");
            synchronizers.setBaseApi("https://qyapi.weixin.qq.com");
            super.saveOrUpdate(synchronizers);

        }
    }

    @Transactional
    @Override
    public boolean save(Synchronizers entity) {
        if (Objects.nonNull(entity) && entity.getId().intValue() == 1) {
            cacheService.deleteObject(ConstsCacheData.SYNC_LDAP_KEY);
        }
        return super.save(entity);
    }

    @Transactional
    @Override
    public boolean updateById(Synchronizers entity) {
        if (Objects.nonNull(entity) && entity.getId().intValue() == 1) {
            cacheService.deleteObject(ConstsCacheData.SYNC_LDAP_KEY);
        }
        return super.updateById(entity);
    }



    /**
     * 链接LDAP
     * @param synchronizers
     * @return
     */
    private boolean testLdap(Synchronizers synchronizers){
        LdapUtils utils = new LdapUtils(
                synchronizers.getBaseApi(),
                synchronizers.getClientId(),
                synchronizers.getClientSecret(),
                synchronizers.getBaseDn());
        DirContext dirContext = null;
        try {
            dirContext = utils.openConnection();
            if (dirContext != null) {
                return true;
            }
        } catch (Exception e){
            throw new BusinessException(400, "无法连接openldap");
        } finally {
            if (Objects.nonNull(dirContext)) {
                try {dirContext.close();} catch (Exception e){}
            }
        }
        return false;
    }
    /**
     * 链接飞书
     * @param synchronizers
     * @return
     */
    private boolean testFeishu(Synchronizers synchronizers){
        if(StringUtils.isEmpty(synchronizers.getBaseApi())) {
            synchronizers.setBaseApi(ConstsSynchronizers.BaseApi.FEISHU);
        }
        try {
            Client client = Client.newBuilder(synchronizers.getClientId(), synchronizers.getClientSecret())
                    .openBaseUrl(synchronizers.getBaseApi())
                    .marketplaceApp() // 设置 app 类型为商店应用
                    .disableTokenCache()
                    .requestTimeout(30, TimeUnit.SECONDS) // 设置httpclient 超时时间，默认永不超时
                    .logReqAtDebug(true) // 在 debug 模式下会打印 http 请求和响应的 headers,body 等信息。
                    .build();
            // 发起token请求
            AppAccessTokenResp tokenResp = client.ext().getAppAccessTokenBySelfBuiltApp(
                    SelfBuiltAppAccessTokenReq.newBuilder()
                            .appSecret(synchronizers.getClientSecret())
                            .appId(synchronizers.getClientId())
                            .build());
            //如果能获取token则任务链接凭证正常
            if (tokenResp.success()) {
                return true;
            } else {
                throw new BusinessException(400, "无法连接飞书");
            }
        } catch (Exception e){
            log.error("error",e);
            if(e instanceof BusinessException) {
                BusinessException businessException = (BusinessException) e;
                throw businessException;
            } else {
                throw new BusinessException(400, "无法连接飞书");
            }
        }
    }
    /**
     * 链接钉钉
     * @param synchronizers
     * @return
     */
    private boolean testDingding(Synchronizers synchronizers){
        try {
            if (StringUtils.isEmpty(synchronizers.getBaseApi())) {
                synchronizers.setBaseApi(ConstsSynchronizers.BaseApi.DINGDING);
            }
            DingTalkClient client = new DefaultDingTalkClient(synchronizers.getBaseApi()+"/gettoken");
            OapiGettokenRequest request = new OapiGettokenRequest();
            request.setAppkey(synchronizers.getClientId());
            request.setAppsecret(synchronizers.getClientSecret());
            request.setHttpMethod("GET");
            OapiGettokenResponse response = client.execute(request);
            log.info("response : " + response.getBody());
            if (response.getErrcode() == 0) {
                return true;
            } else {
                throw new BusinessException(400,response.getErrmsg());
            }
        } catch (Exception e){
            log.error("error",e);
            if(e instanceof BusinessException) {
                BusinessException businessException = (BusinessException) e;
                throw businessException;
            } else {
                log.error("error",e);
                throw new BusinessException(400, "无法链接钉钉");
            }
        }
    }

    /**
     * 链接企业微信
     * @param synchronizers
     * @return
     */
    private boolean testWorkWeixin(Synchronizers synchronizers){
        try {
            if (StringUtils.isEmpty(synchronizers.getBaseApi())) {
                synchronizers.setBaseApi(ConstsSynchronizers.BaseApi.WORKWEIXIN);
            }
            WorkWeixinClient client = WorkWeixinClient
                    .newBuilder(synchronizers.getClientId(),
                            synchronizers.getUserClientSecret(),
                            synchronizers.getClientSecret(),
                            synchronizers.getBaseApi())
                    .build();
            //通讯录token用于创建和修改信息的凭证
            String userAccessToken = client.getUserAccessToken();
            if (StringUtils.isEmpty(userAccessToken)) {
                log.error("获取通讯录token失败...");
                throw new BusinessException(400, "获取通讯录token失败");
            }
            //APP应用token查询部门列表，需要设置应用可见范围,用于查询部门列表
            String appToken = client.getAppToken();
            if (StringUtils.isEmpty(appToken)) {
                log.error("获取应用token失败...");
                throw new BusinessException(400, "获取应用token失败");
            }
            return true;
        } catch (Exception e){
            log.error("error",e);
            if(e instanceof BusinessException) {
                BusinessException businessException = (BusinessException) e;
                throw businessException;
            } else {
                log.error("error",e);
                throw new BusinessException(400, "无法链接企业微信");
            }
        }
    }

}
