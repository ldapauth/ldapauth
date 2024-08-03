package com.ldapauth.persistence.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.constants.ConstsStatus;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.persistence.mapper.SocialsProviderMapper;
import com.ldapauth.persistence.service.SocialsProviderService;
import com.ldapauth.pojo.dto.CallbackDTO;
import com.ldapauth.pojo.dto.SocialsProviderQueryDTO;
import com.ldapauth.pojo.entity.SocialsProvider;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class SocialsProviderServiceImpl extends ServiceImpl<SocialsProviderMapper, SocialsProvider> implements SocialsProviderService {

    @Autowired
    CacheService cacheService;

    @Autowired
    IdentifierGenerator identifierGenerator;

    @Override
    public String generate() {
        return String.valueOf(identifierGenerator.nextId(SocialsProvider.class));
    }

    @Override
    public Page<SocialsProvider> fetch(SocialsProviderQueryDTO queryDTO) {
        LambdaQueryWrapper<SocialsProvider> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (Objects.nonNull(queryDTO.getName())) {
            lambdaQueryWrapper.like(SocialsProvider::getName,queryDTO.getName());
        }
        if (Objects.nonNull(queryDTO.getStatus())) {
            lambdaQueryWrapper.eq(SocialsProvider::getStatus,queryDTO.getStatus());
        }
        lambdaQueryWrapper.orderByDesc(SocialsProvider::getCreateTime);
        return super.page(queryDTO.build(),lambdaQueryWrapper);
    }

    @Override
    public List<SocialsProvider> cacheList() {
        return loadCache();
    }

    @Override
    public AuthRequest authorize(Long id) {
        SocialsProvider socialsProvider = getById(id);
        if (Objects.isNull(socialsProvider)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"第三方标识不存在");
        }
        if (socialsProvider.getStatus().intValue() == ConstsStatus.DATA_INACTIVE) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"第三方被禁用");
        }
        String redirectUri = socialsProvider.getRedirectUri();
        String type = socialsProvider.getType().toLowerCase();
        AuthConfig authConfig = AuthConfig.builder()
                .clientId(socialsProvider.getClientId())
                .clientSecret(socialsProvider.getClientSecret())
                .redirectUri(URLUtil.encodeAll(redirectUri))
                .build();
        AuthRequest authRequest = null;
        switch (type) {
            case "wechatopen":
                authRequest = new AuthWeChatOpenRequest(authConfig);
                break;
            case "workweixin":
                authConfig.setAgentId(socialsProvider.getAgentId());
                authRequest = new AuthWeChatEnterpriseQrcodeRequest(authConfig);
                break;
            case "sinaweibo":
                authRequest = new AuthWeiboRequest(authConfig);
                break;
            case "dingtalk":
                authRequest = new AuthDingTalkRequest(authConfig);
                break;
            case "feishu":
                authConfig.setRedirectUri(redirectUri);
                authRequest = new AuthFeishuRequest(authConfig);
                break;
            case "alipay":
                String alipayPublicKey = socialsProvider.getAlipayPublicKey();
                authRequest = new AuthAlipayRequest(authConfig, alipayPublicKey);
                break;
            case "douyin":
                authRequest = new AuthDouyinRequest(authConfig);
                break;
            default:
                throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"未知第三方类型");
        }

        return authRequest;
    }

    @Override
    public AuthUser authCallback(CallbackDTO callbackDTO) {
        SocialsProvider socialsProvider = getById(callbackDTO.getId());
        if (Objects.isNull(socialsProvider)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"第三方标识不存在");
        }
        if (socialsProvider.getStatus().intValue() == ConstsStatus.DATA_INACTIVE) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"第三方被禁用");
        }

        AuthCallback authCallback = new AuthCallback();
        authCallback.setCode(callbackDTO.getCode());
        authCallback.setAuth_code(callbackDTO.getAuth_code());
        authCallback.setOauth_token(callbackDTO.getOauthToken());
        authCallback.setAuthorization_code(callbackDTO.getAuthorization_code());
        authCallback.setOauth_verifier(callbackDTO.getOauthVerifier());
        authCallback.setState(callbackDTO.getState());
        log.debug("Callback OAuth code {}, auth_code {}, oauthToken {}, authorization_code {}, oauthVerifier {} , state {}",
                authCallback.getCode(),
                authCallback.getAuth_code(),
                authCallback.getOauth_token(),
                authCallback.getAuthorization_code(),
                authCallback.getOauth_verifier(),
                authCallback.getState());
        AuthRequest request = this.authorize(callbackDTO.getId());

        if (Objects.isNull(request)) {
            log.error("无法构建认证请求：{},", callbackDTO.getId());
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"无法构建认证请求");
        }
        //获取第三方凭证响应对象
        AuthResponse<AuthUser> authResponse = request.login(authCallback);
        log.debug("Response  : " + authResponse);
        if (authResponse.ok()) {
            AuthUser user = authResponse.getData();
            user.setCompany(socialsProvider.getName());
            return user;
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"无法获取第三方授权用户");
    }



    @Override
    public boolean saveOrUpdate(SocialsProvider entity) {
        boolean flag = super.saveOrUpdate(entity);
        removeCache();
        return flag;
    }

    @Override
    public boolean removeByIds(Collection<?> list) {
        boolean flag = super.removeByIds(list);
        removeCache();
        return flag;
    }

    /**
     * 移除缓存
     */
    private void removeCache(){
        cacheService.deleteObject(ConstsCacheData.SOCIALS_PROVIDER_DATA_LIST_KEY);
        loadCache();
    }
    /**
     * 加载缓存
     */
    @PostConstruct
    private List<SocialsProvider> loadCache(){
        List<SocialsProvider> list = cacheService.getCacheObject(ConstsCacheData.SOCIALS_PROVIDER_DATA_LIST_KEY);
        if (CollectionUtil.isNotEmpty(list)) {
            return list;
        }
        list = super.list();
        Iterator<SocialsProvider> it = list.iterator();
        while (it.hasNext()) {
            SocialsProvider socialsProvider = it.next();
            //如果不等于正常状态，则移除
            if (socialsProvider.getStatus().intValue() != ConstsStatus.DATA_ACTIVE) {
                it.remove();;
            }
        }
        //设置缓存
        cacheService.setCacheObject(ConstsCacheData.SOCIALS_PROVIDER_DATA_LIST_KEY,list);
        return list;
    }
}
