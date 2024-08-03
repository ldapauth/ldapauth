package com.ldapauth.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;

import com.ldapauth.domain.Config;
import com.ldapauth.utils.request.WorkWeixinRequest;
import com.ldapauth.utils.responce.WorkWeixinTokenResponce;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.util.Objects;

@Slf4j
public class WorkWeixinClient {

    /**
     * 配置应用凭证
     */
    private Config config;


    /**
     * 组织请求
     */
    private WorkWeixinRequest deptmentRequest;


    public WorkWeixinRequest deptment() {
        return deptmentRequest;
    }


    final static String TOKEN_CACHE = "SYNC:WORKWEIXIN:CACHE:TOKEN:";

    /**
     * 获取通讯录token
     * @return
     */
     public String getUserAccessToken(){
        String tokenKey = TOKEN_CACHE+config.getAppId()+":"+config.getUserinfoSecret();
        String tokenURI = config.getBaseUri()+"/cgi-bin/gettoken?corpid="+config.getAppId()+"&corpsecret="+config.getUserinfoSecret();
        String body = HttpUtil.get(tokenURI);
        if (StringUtils.isNotBlank(body)) {
            WorkWeixinTokenResponce responce = JSON.parseObject(body, WorkWeixinTokenResponce.class);
            if (StringUtils.isNotBlank(responce.getAccess_token())) {
                return responce.getAccess_token();
            }
        }
        log.error("gettoken uri [{}] responce {}",tokenURI,body);
        return null;
    }

    /**
     * 获取应用token
     * @return
     */
    public String getAppToken(){
        String tokenKey = TOKEN_CACHE+config.getAppId()+":"+config.getAppClientSecret();
        String tokenURI = config.getBaseUri()+"/cgi-bin/gettoken?corpid="+config.getAppId()+"&corpsecret="+config.getAppClientSecret();
        String body = HttpUtil.get(tokenURI);
        if (StringUtils.isNotBlank(body)) {
            WorkWeixinTokenResponce responce = JSON.parseObject(body, WorkWeixinTokenResponce.class);
            if (StringUtils.isNotBlank(responce.getAccess_token())) {
                return responce.getAccess_token();
            }
        }
        log.error("gettoken uri [{}] responce {}",tokenURI,body);
        return null;
    }

    /**
     * 默认飞书地址
     */
    private static final String defbaseUri = "https://qyapi.weixin.qq.com";

    public static Builder newBuilder(String appId, String userinfoSecret,String appSecret, String baseUri) {
        return new Builder(appId, userinfoSecret,appSecret,baseUri);
    }

    public static final class Builder {
        private Config config = new Config();
        private Builder(String appId,String userinfoSecret, String appSecret,String baseUri) {
            config.setAppId(appId);
            config.setUserinfoSecret(userinfoSecret);
            config.setAppClientSecret(appSecret);
            if (Objects.nonNull(baseUri)) {
                config.setBaseUri(baseUri);
            } else {
                config.setBaseUri(defbaseUri);
            }
        }

        public WorkWeixinClient build() {
            WorkWeixinClient client = new WorkWeixinClient();
            client.setConfig(config);
            client.deptmentRequest = new WorkWeixinRequest(config);
            return client;
        }
    }

    public void setConfig(Config config) {
        this.config = config;
    }



}
