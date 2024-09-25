package com.ldapauth.persistence.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.constants.IDPCacheConstants;
import com.ldapauth.crypto.Base64Utils;
import com.ldapauth.crypto.DigestUtils;
import com.ldapauth.crypto.ReciprocalUtils;
import com.ldapauth.crypto.cert.X509CertUtils;
import com.ldapauth.crypto.keystore.KeyStoreLoader;
import com.ldapauth.crypto.keystore.KeyStoreUtil;
import com.ldapauth.crypto.password.PasswordReciprocal;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.persistence.mapper.*;
import com.ldapauth.persistence.service.ClientAppsService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.apps.ClientApps;
import com.ldapauth.pojo.entity.apps.details.ClientAppsCASDetails;
import com.ldapauth.pojo.entity.apps.details.ClientAppsJWTDetails;
import com.ldapauth.pojo.entity.apps.details.ClientAppsOIDCDetails;
import com.ldapauth.pojo.entity.apps.details.ClientAppsSAMLDetails;
import com.ldapauth.pojo.vo.Result;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.Requirement;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @Author : 15829 //作者
 * @Date: 2024/7/9  15:51
 */
@Slf4j
@Service
public class ClientAppsServiceImpl extends ServiceImpl<ClientAppsMapper, ClientApps> implements ClientAppsService {
    @Autowired
    AppsOidcDetailsMapper appsOidcDetailsMapper;

    @Autowired
    AppsSamlDetailsMapper appsSamlDetailsMapper;

    @Autowired
    AppsJwtDetailsMapper appsJwtDetailsMapper;

    @Autowired
    AppsCasDetailsMapper appsCasDetailsMapper;

    @Autowired
    ClientAppsMapper mapper;

    @Autowired
    IdentifierGenerator identifierGenerator;

    @Autowired
    KeyStoreLoader keyStoreLoader;

    @Autowired
    CacheService cacheService;
    @Override
    public List<ClientApps> myApps(Long userId) {
        return mapper.myApps(userId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    @Override
    public Result<String> oidcCreate(AppsOidcDTO dto) {
        //SignedPrincipal signedPrincipal = AuthorizationUtils.getCurrentUser();
        LambdaQueryWrapper<ClientApps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }

        ClientApps app = BeanUtil.copyProperties(dto.getApp(), ClientApps.class);
        //app.setInstId(signedPrincipal.getInstId());
        app.setProtocol(0);
        //生成clientId和密钥
        buildClient(app);
        encodeSecret(app);
        super.save(app);
        ClientAppsOIDCDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientAppsOIDCDetails.class);
        details.setAppId(app.getId());
        //details.setInstId(signedPrincipal.getInstId());
        details.setClientId(app.getClientId());
        details.setClientSecret(app.getClientSecret());
        //存在签名
        if(details.getIsSignature().equalsIgnoreCase(IDPCacheConstants.YES)) {
            details.setSignatureKey(getSignatureSecret(app.getId(), details.getSignature()));
        } else {
            details.setSignatureKey("");
        }
        appsOidcDetailsMapper.insert(details);
        //details.setDeviceType(app.getDeviceType());
        //RedisUtils.setCacheObject((IDPCacheConstants.OIDC_CACHE_PREFIX + details.getClientId()),details, Duration.ofDays(7));
        return Result.success("新增成功");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    @Override
    public Result<String> oidcUpdate(AppsOidcDTO dto) {
        //SignedPrincipal signedPrincipal = AuthorizationUtils.getCurrentUser();
        LambdaQueryWrapper<ClientApps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        //排除当前ID
        queryWrapper.notIn(ClientApps::getId,dto.getApp().getId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        queryWrapper.notIn(ClientApps::getId,dto.getApp().getId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }



        ClientApps app = BeanUtil.copyProperties(dto.getApp(), ClientApps.class);
        app.setProtocol(0);
        app.setClientSecret(dto.getDetails().getClientSecret());
        encodeSecret(app);

        super.updateById(app);
        ClientAppsOIDCDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientAppsOIDCDetails.class) ;
        details.setAppId(app.getId());
        details.setStatus(app.getStatus());
        details.setClientSecret(app.getClientSecret());
        //details.setInstId(signedPrincipal.getInstId());

        LambdaQueryWrapper<ClientAppsOIDCDetails> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ClientAppsOIDCDetails::getAppId,app.getId());
        //updateWrapper.eq(AppsOAuth2ClientDetails::getInstId,app.getInstId());

        ClientAppsOIDCDetails oidcDetails = appsOidcDetailsMapper.selectOne(updateWrapper);
        //存在签名
        if(details.getIsSignature().equalsIgnoreCase(IDPCacheConstants.YES)) {
            //更新时判断之前的算法和更新的算法是否一直，一直则不更新签名密钥
            if(!oidcDetails.getSignature().equalsIgnoreCase(details.getSignature())) {
                details.setSignatureKey(getSignatureSecret(app.getId(), details.getSignature()));
            }
        } else {
            details.setSignatureKey("");
        }

        appsOidcDetailsMapper.update(details,updateWrapper);
        //details.setDeviceType(app.getDeviceType());
        //RedisUtils.setCacheObject((IDPCacheConstants.OIDC_CACHE_PREFIX + details.getClientId()),details,Duration.ofDays(7));
        return Result.success("修改成功");
    }


    private void encodeSecret(ClientApps app){
        if(StringUtils.isNotBlank(app.getClientSecret())){
            String encodeSecret = PasswordReciprocal.getInstance().encode(app.getClientSecret());
            app.setClientSecret(encodeSecret);
        }
    }

    private void decoderSecret(ClientApps app){
        try {
            if (StringUtils.isNotBlank(app.getClientSecret())) {
                String decodeSecret = PasswordReciprocal.getInstance().decoder(app.getClientSecret());
                app.setClientSecret(decodeSecret);
            }
        }catch (Exception e){
           log.error("error");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    @Override
    public Result<String> samlCreate(AppsSamlDTO dto) {
        //SignedPrincipal signedPrincipal = AuthorizationUtils.getCurrentUser();
        LambdaQueryWrapper<ClientApps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }
        ClientApps app = BeanUtil.copyProperties(dto.getApp(), ClientApps.class);
        //app.setInstId(signedPrincipal.getInstId());
        app.setProtocol(1);
        //生成clientId和密钥
        buildClient(app);
        encodeSecret(app);
        super.save(app);
        ClientAppsSAMLDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientAppsSAMLDetails.class);
        if (StringUtils.isNotEmpty(dto.getDetails().getB64Encoder())) {
            String b64Encoder = dto.getDetails().getB64Encoder();
            try {
                X509Certificate trustCert = X509CertUtils.loadCertFromB64Encoded(b64Encoder);
                if(Objects.nonNull(trustCert)) {
                    details.setTrustCert(trustCert);
                    KeyStore keyStore = KeyStoreUtil.clone(keyStoreLoader.getKeyStore(), keyStoreLoader.getKeystorePassword());
                    KeyStore trustKeyStore = null;
                    if (StringUtils.isNotEmpty(details.getEntityId())) {
                        trustKeyStore = KeyStoreUtil.importTrustCertificate(keyStore, details.getTrustCert(), details.getEntityId());
                    } else {
                        trustKeyStore = KeyStoreUtil.importTrustCertificate(keyStore, details.getTrustCert());
                    }
                    byte[] keyStoreByte = KeyStoreUtil.keyStore2Bytes(trustKeyStore, keyStoreLoader.getKeystorePassword());
                    // store KeyStore content
                    details.setKeystore(keyStoreByte);
                }
            } catch (Exception e){
                log.error("error",e);
                throw new BusinessException(400,"无法解析元数据文件");
            }
        }
        details.setAppId(app.getId());
        details.setStatus(app.getStatus());
        //details.setInstId(signedPrincipal.getInstId());
        appsSamlDetailsMapper.insert(details);

        //details.setDeviceType(app.getDeviceType());

        //String value = ObjectTransformer.serialize(details);
        //RedisUtils.setCacheObject((IDPCacheConstants.SAML_CACHE_PREFIX + details.getAppId()),value,Duration.ofDays(7));
        return Result.success("新增成功");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    @Override
    public Result<String> samlUpdate(AppsSamlDTO dto) {
        //SignedPrincipal signedPrincipal = AuthorizationUtils.getCurrentUser();
        LambdaQueryWrapper<ClientApps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        //排除当前ID
        queryWrapper.notIn(ClientApps::getId,dto.getApp().getId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(ClientApps::getId,dto.getApp().getId());
        queryWrapper.eq(ClientApps::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }
        ClientApps app = BeanUtil.copyProperties(dto.getApp(), ClientApps.class);
        app.setProtocol(1);
        encodeSecret(app);
        super.updateById(app);
        ClientAppsSAMLDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientAppsSAMLDetails.class);

        if (StringUtils.isNotEmpty(dto.getDetails().getB64Encoder())) {
            String b64Encoder = dto.getDetails().getB64Encoder();
            try {
                X509Certificate trustCert = X509CertUtils.loadCertFromB64Encoded(b64Encoder);
                if(Objects.nonNull(trustCert)) {
                    details.setTrustCert(trustCert);
                    KeyStore keyStore = KeyStoreUtil.clone(keyStoreLoader.getKeyStore(), keyStoreLoader.getKeystorePassword());
                    KeyStore trustKeyStore = null;
                    if (StringUtils.isNotEmpty(details.getEntityId())) {
                        trustKeyStore = KeyStoreUtil.importTrustCertificate(keyStore, details.getTrustCert(), details.getEntityId());
                    } else {
                        trustKeyStore = KeyStoreUtil.importTrustCertificate(keyStore, details.getTrustCert());
                    }
                    byte[] keyStoreByte = KeyStoreUtil.keyStore2Bytes(trustKeyStore, keyStoreLoader.getKeystorePassword());
                    // store KeyStore content
                    details.setKeystore(keyStoreByte);
                }
            }catch (Exception e){
                throw new BusinessException(400,"无法解析元数据文件");
            }
        }
        details.setAppId(app.getId());
        details.setStatus(app.getStatus());
        //details.setInstId(signedPrincipal.getInstId());

        LambdaQueryWrapper<ClientAppsSAMLDetails> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ClientAppsSAMLDetails::getAppId,app.getId());
        //updateWrapper.eq(AppsSamlDetails::getInstId,app.getInstId());
        appsSamlDetailsMapper.update(details,updateWrapper);
        //details.setDeviceType(app.getDeviceType());
        //String value = ObjectTransformer.serialize(details);
        //RedisUtils.setCacheObject((IDPCacheConstants.SAML_CACHE_PREFIX + details.getAppId()),value,Duration.ofDays(7));
        return Result.success("修改成功");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    @Override
    public Result<String> jwtCreate(AppsJwtDTO dto) {
        //SignedPrincipal signedPrincipal = AuthorizationUtils.getCurrentUser();
        LambdaQueryWrapper<ClientApps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }
        ClientApps app = BeanUtil.copyProperties(dto.getApp(), ClientApps.class);
        //app.setInstId(signedPrincipal.getInstId());
        app.setProtocol(2);
        //生成clientId和密钥
        buildClient(app);
        encodeSecret(app);
        super.save(app);
        ClientAppsJWTDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientAppsJWTDetails.class) ;
        details.setAppId(app.getId());
        //details.setInstId(signedPrincipal.getInstId());
        details.setStatus(app.getStatus());
        //存在签名
        if(details.getIsSignature().equalsIgnoreCase(IDPCacheConstants.YES)) {
            details.setSignatureKey(getSignatureSecret(app.getId(),details.getSignature()));
        } else {
            details.setSignatureKey("");
        }
        appsJwtDetailsMapper.insert(details);
        //details.setDeviceType(app.getDeviceType());
        //RedisUtils.setCacheObject(IDPCacheConstants.JWT_CACHE_PREFIX+app.getId(),details,Duration.ofDays(7));
        return Result.success("新增成功");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    @Override
    public Result<String> jwtUpdate(AppsJwtDTO dto) {
        //SignedPrincipal signedPrincipal = AuthorizationUtils.getCurrentUser();
        LambdaQueryWrapper<ClientApps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        //排除当前ID
        queryWrapper.notIn(ClientApps::getId,dto.getApp().getId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(ClientApps::getId,dto.getApp().getId());
        queryWrapper.eq(ClientApps::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }
        ClientApps app = BeanUtil.copyProperties(dto.getApp(), ClientApps.class);
        app.setProtocol(2);
        encodeSecret(app);
        super.updateById(app);
        ClientAppsJWTDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientAppsJWTDetails.class) ;
        details.setAppId(app.getId());
        //details.setInstId(signedPrincipal.getInstId());
        details.setStatus(app.getStatus());

        LambdaQueryWrapper<ClientAppsJWTDetails> selectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        selectLambdaQueryWrapper.eq(ClientAppsJWTDetails::getAppId,app.getId());
        //selectLambdaQueryWrapper.eq(AppsJwtDetails::getInstId,app.getInstId());
        ClientAppsJWTDetails jwtDetails = appsJwtDetailsMapper.selectOne(selectLambdaQueryWrapper);
        //存在签名
         if(details.getIsSignature().equalsIgnoreCase(IDPCacheConstants.YES)) {
            //更新时判断之前的算法和更新的算法是否一直，一直则不更新签名密钥
            if(!jwtDetails.getSignature().equalsIgnoreCase(details.getSignature())) {
                details.setSignatureKey(getSignatureSecret(app.getId(), details.getSignature()));
            }
        } else {
            details.setSignatureKey("");
        }
        LambdaQueryWrapper<ClientAppsJWTDetails> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ClientAppsJWTDetails::getAppId,app.getId());
        //updateWrapper.eq(AppsJwtDetails::getInstId,app.getInstId());
        appsJwtDetailsMapper.update(details,updateWrapper);
        //details.setDeviceType(app.getDeviceType());
        //RedisUtils.setCacheObject((IDPCacheConstants.JWT_CACHE_PREFIX+app.getId()),details,Duration.ofDays(7));
        return Result.success("修改成功");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    @Override
    public Result<String> casCreate(AppsCasDTO dto) {
        //SignedPrincipal signedPrincipal = AuthorizationUtils.getCurrentUser();
        LambdaQueryWrapper<ClientApps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppName,dto.getApp().getAppName());
       // queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppCode,dto.getApp().getAppCode());
       // queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }

        ClientApps app = BeanUtil.copyProperties(dto.getApp(), ClientApps.class);
        //app.setInstId(signedPrincipal.getInstId());
        app.setProtocol(3);
        //生成clientId和密钥
        buildClient(app);
        encodeSecret(app);
        super.save(app);
        ClientAppsCASDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientAppsCASDetails.class) ;
        details.setAppId(app.getId());
        details.setStatus(app.getStatus());
        //details.setInstId(signedPrincipal.getInstId());
        appsCasDetailsMapper.insert(details);
        cacheService.deleteObject(ConstsCacheData.CAS_CACGE_LIST);
        //loadCasAllCache();
        return Result.success("新增成功");
    }
    @Transactional(isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    @Override
    public Result<String> casUpdate(AppsCasDTO dto) {
        //SignedPrincipal signedPrincipal = AuthorizationUtils.getCurrentUser();
        LambdaQueryWrapper<ClientApps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        //排除当前ID
        queryWrapper.notIn(ClientApps::getId,dto.getApp().getId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(ClientApps::getId,dto.getApp().getId());
        queryWrapper.eq(ClientApps::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }
        ClientApps app = BeanUtil.copyProperties(dto.getApp(), ClientApps.class);
        app.setProtocol(3);
        encodeSecret(app);
        super.updateById(app);
        ClientAppsCASDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientAppsCASDetails.class) ;
        details.setAppId(app.getId());
        details.setStatus(app.getStatus());
        //details.setInstId(signedPrincipal.getInstId());
        LambdaQueryWrapper<ClientAppsCASDetails> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ClientAppsCASDetails::getAppId,app.getId());
        //updateWrapper.eq(AppsCasDetails::getInstId,app.getInstId());
        appsCasDetailsMapper.update(details,updateWrapper);
        cacheService.deleteObject(ConstsCacheData.CAS_CACGE_LIST);
       // loadCasAllCache();
        return Result.success("修改成功");
    }

    @Override
    public Result<AppsDetails> getDetails(Long id) {
        ClientApps app = super.getById(id);
        if (Objects.isNull(app)) {
            return Result.failed("查询失败");
        }
        decoderSecret(app);
        //获取oidc扩展配置
        if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 0) {
            AppsDetails<ClientAppsOIDCDetails> details = new AppsDetails();
            details.setApp(app);
            LambdaQueryWrapper<ClientAppsOIDCDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientAppsOIDCDetails::getAppId,id);
            //queryWrapper.eq(AppsOAuth2ClientDetails::getInstId,app.getInstId());
            ClientAppsOIDCDetails oauthDetails = appsOidcDetailsMapper.selectOne(queryWrapper);
            if (Objects.nonNull(oauthDetails)) {
                oauthDetails.setClientSecret(app.getClientSecret());
                details.setDetails(oauthDetails);
            }
            return Result.success(details);
        }
        //获取saml扩展配置
        else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 1) {
            AppsDetails<ClientAppsSAMLDetails> details = new AppsDetails();
            details.setApp(app);
            LambdaQueryWrapper<ClientAppsSAMLDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientAppsSAMLDetails::getAppId,id);
            //queryWrapper.eq(AppsSamlDetails::getInstId,app.getInstId());
            ClientAppsSAMLDetails samlDetails = appsSamlDetailsMapper.selectOne(queryWrapper);
            if (Objects.nonNull(samlDetails)) {
                details.setDetails(samlDetails);
            }
            return Result.success(details);
        }
        //获取jwt扩展配置
        else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 2) {
            AppsDetails<ClientAppsJWTDetails> details = new AppsDetails();
            details.setApp(app);
            LambdaQueryWrapper<ClientAppsJWTDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientAppsJWTDetails::getAppId,id);
            //queryWrapper.eq(AppsJwtDetails::getInstId,app.getInstId());
            ClientAppsJWTDetails jwtDetails = appsJwtDetailsMapper.selectOne(queryWrapper);
            if (Objects.nonNull(jwtDetails)) {
                details.setDetails(jwtDetails);
            }
            return Result.success(details);
        }
        //获取cas扩展配置
        else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 3) {
            AppsDetails<ClientAppsCASDetails> details = new AppsDetails();
            details.setApp(app);
            LambdaQueryWrapper<ClientAppsCASDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientAppsCASDetails::getAppId,id);
            //queryWrapper.eq(AppsCasDetails::getInstId,app.getInstId());
            ClientAppsCASDetails casdetails = appsCasDetailsMapper.selectOne(queryWrapper);
            if (Objects.nonNull(casdetails)) {
                details.setDetails(casdetails);
            }
            return Result.success(details);
        }
        AppsDetails details = new AppsDetails();
        details.setApp(app);
        return Result.success(details);
    }

    @Override
    public AppsDetails<ClientAppsOIDCDetails> getDetailsClientId(String clientId) {
        LambdaQueryWrapper<ClientApps> appQuery = new LambdaQueryWrapper();
        appQuery.eq(ClientApps::getClientId,clientId);
        ClientApps app = super.getOne(appQuery);
        if (Objects.isNull(app)) {
             appQuery = new LambdaQueryWrapper();
             appQuery.eq(ClientApps::getId,clientId);
             app = super.getOne(appQuery);
        }
        if (Objects.isNull(app)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "无法获取应用对象");
        }
        Long id = app.getId();
        //获取oidc扩展配置
        if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 0) {
            AppsDetails<ClientAppsOIDCDetails> details = new AppsDetails();
            details.setApp(app);
            LambdaQueryWrapper<ClientAppsOIDCDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientAppsOIDCDetails::getAppId,id);
            ClientAppsOIDCDetails oauthDetails = appsOidcDetailsMapper.selectOne(queryWrapper);
            if (Objects.isNull(oauthDetails)) {
                throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"无法获取扩展属性");
            }
            details.setDetails(oauthDetails);
            return details;
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"无法获取扩展属性");
    }

    @Override
    public int updateStatus(ArrayList<Long> ids, int i) {
        int count = mapper.update(new LambdaUpdateWrapper<ClientApps>().in(ClientApps::getId,ids).set(ClientApps::getStatus,i));
        if (count > 0) {
            for(Long id : ids) {
                ClientApps app = super.getById(id);
                //更新oauth扩展配置
                if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 0) {
                    LambdaQueryWrapper<ClientAppsOIDCDetails> updateWrapper = new LambdaQueryWrapper<>();
                    updateWrapper.eq(ClientAppsOIDCDetails::getAppId,id);
                    //updateWrapper.eq(AppsOAuth2ClientDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                    ClientAppsOIDCDetails details = new ClientAppsOIDCDetails();
                    details.setStatus(i);
                    appsOidcDetailsMapper.update(details,updateWrapper);
                    //details = appsOAuth2ClientDetailsMapper.selectOne(updateWrapper);
                    //details.setDeviceType(app.getDeviceType());
                    //RedisUtils.setCacheObject(IDPCacheConstants.OAUTH_CACHE_PREFIX+app.getClientId(),details,Duration.ofDays(7));
                }
                //更新saml扩展配置
                else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 1) {
                    LambdaQueryWrapper<ClientAppsSAMLDetails> updateWrapper = new LambdaQueryWrapper<>();
                    updateWrapper.eq(ClientAppsSAMLDetails::getAppId,id);
                    //updateWrapper.eq(AppsSamlDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                    ClientAppsSAMLDetails details = new ClientAppsSAMLDetails();
                    details.setStatus(i);
                    appsSamlDetailsMapper.update(details,updateWrapper);
                    //details = appsSamlDetailsMapper.selectOne(updateWrapper);
                    //details.setDeviceType(app.getDeviceType());
                    //String value = ObjectTransformer.serialize(details);
                    //RedisUtils.setCacheObject(IDPCacheConstants.SAML_CACHE_PREFIX + id,value,Duration.ofDays(7));
                }
                //更新jwt扩展配置
                else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 2) {
                    LambdaQueryWrapper<ClientAppsJWTDetails> updateWrapper = new LambdaQueryWrapper<>();
                    updateWrapper.eq(ClientAppsJWTDetails::getAppId,id);
                    //updateWrapper.eq(AppsJwtDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                    ClientAppsJWTDetails details = new ClientAppsJWTDetails();
                    details.setStatus(i);
                    appsJwtDetailsMapper.update(details,updateWrapper);
                    //details = AppsJwtDetailsMapper.selectOne(updateWrapper);
                    //details.setDeviceType(app.getDeviceType());
                    //RedisUtils.setCacheObject(IDPCacheConstants.JWT_CACHE_PREFIX+app.getId(),details,Duration.ofDays(7));

                }
                //更新cas扩展配置
                else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 3) {
                    LambdaQueryWrapper<ClientAppsCASDetails> updateWrapper = new LambdaQueryWrapper<>();
                    updateWrapper.eq(ClientAppsCASDetails::getAppId,id);
                    //updateWrapper.eq(AppsCasDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                    ClientAppsCASDetails details = new ClientAppsCASDetails();
                    details.setStatus(i);
                    appsCasDetailsMapper.update(details,updateWrapper);
                }
            }
            cacheService.deleteObject(ConstsCacheData.CAS_CACGE_LIST);
        }
        return count;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    @Override
    public int deleteBatch(ArrayList<Long> ids) {
        List<ClientApps> list = this.listByIds(ids);
        for(ClientApps app :list) {
            //检查是否存在启用的应用
            if (app.getStatus().intValue() == 0) {
                throw new BusinessException(400,"请先禁用应用后在删除");
            }
        }
        for (Long id : ids) {
            ClientApps app = super.getById(id);
            //更新oauth扩展配置
            if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 0) {
                LambdaQueryWrapper<ClientAppsOIDCDetails> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ClientAppsOIDCDetails::getAppId,id);
                //wrapper.eq(AppsOAuth2ClientDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                appsOidcDetailsMapper.delete(wrapper);
                //RedisUtils.deleteObject(IDPCacheConstants.OAUTH_CACHE_PREFIX+app.getClientId());
            }
            //更新saml扩展配置
            else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 1) {
                LambdaQueryWrapper<ClientAppsSAMLDetails> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ClientAppsSAMLDetails::getAppId,id);
                //wrapper.eq(AppsSamlDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                appsSamlDetailsMapper.delete(wrapper);
                //RedisUtils.deleteObject(IDPCacheConstants.SAML_CACHE_PREFIX + id);
            }
            //更新jwt扩展配置
            else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 2) {
                LambdaQueryWrapper<ClientAppsJWTDetails> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ClientAppsJWTDetails::getAppId,id);
                //wrapper.eq(AppsJwtDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                appsJwtDetailsMapper.delete(wrapper);
                //RedisUtils.deleteObject(IDPCacheConstants.JWT_CACHE_PREFIX+app.getId());
            }
            //删除cas扩展配置
            else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 3) {
                LambdaQueryWrapper<ClientAppsCASDetails> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ClientAppsCASDetails::getAppId,id);
                //wrapper.eq(AppsCasDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                appsCasDetailsMapper.delete(wrapper);
            }
            //删除应用
            super.removeById(id);
        }
        cacheService.deleteObject(ConstsCacheData.CAS_CACGE_LIST);
        return 1;
    }
    AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public ClientAppsCASDetails getAppDetails(String service, boolean isMach) {
        List<ClientAppsCASDetails> list =  cacheService.getCacheObject(ConstsCacheData.CAS_CACGE_LIST);
        if(Objects.isNull(list)) {
            list = appsCasDetailsMapper.selectList(new LambdaQueryWrapper<>());
            cacheService.setCacheObject(ConstsCacheData.CAS_CACGE_LIST,list);
        }
        if (CollectionUtil.isNotEmpty(list)) {
           for(ClientAppsCASDetails details : list) {
               //匹配url
               String[] serviceList = StringUtils.split(service,",");
               for (String dbservice : serviceList) {
                   if (matcher.match(dbservice, service)) {
                       return details;
                   }
               }
           }
        }
        return null;
    }

    @Override
    public ClientAppsJWTDetails getAppsJwtDetails(Long appId) {
        LambdaQueryWrapper<ClientAppsJWTDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientAppsJWTDetails::getAppId,appId);
        return appsJwtDetailsMapper.selectOne(queryWrapper);
    }

    @Override
    public ClientApps getByClientId(String clientId, boolean isMach) {
        LambdaQueryWrapper<ClientApps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientApps::getClientId,clientId);
        return super.getOne(queryWrapper);
    }

    private void buildClient(ClientApps app){
        Long id = identifierGenerator.nextId(app).longValue();
        String clientId = Base64Utils.base64UrlEncode((String.valueOf(id)).getBytes());
        app.setClientId(clientId);
        String accessSecret = DigestUtils.md5Hex(String.valueOf(id));
        StringBuffer accessSecretBuffer = new StringBuffer();
        //随机转换大小写
        Random rand = new Random();
        for(char c : accessSecret.toCharArray()) {
            accessSecretBuffer.append(rand.nextInt(10) > 5 ? Character.toLowerCase(c):Character.toUpperCase(c));
        }
        app.setClientSecret(accessSecretBuffer.toString());
    }

   /* private void loadCasAllCache() {
        SignedPrincipal signedPrincipal = AuthorizationUtils.getCurrentUser();
        LambdaQueryWrapper<AppsCasDetails> queryWrapper4 = new LambdaQueryWrapper<>();
        queryWrapper4.eq(AppsCasDetails::getInstId, signedPrincipal.getInstId());
        List<AppsCasDetails> list4 = AppsCasDetailsMapper.selectList(queryWrapper4);
        //cas为一个租户一个集合对象，因service在不同租户中可能重复切存在通配符
        RedisUtils.deleteObject(IDPCacheConstants.CAS_CACHE_PREFIX + signedPrincipal.getInstId());
        if (Objects.nonNull(list4) && !list4.isEmpty() && list4.size() > 0) {
            for (AppsCasDetails details : list4) {
                Apps Apps = super.getById(details.getAppId());
                if (Objects.nonNull(Apps)) {
                    details.setDeviceType(Apps.getDeviceType());
                }
            }
            RedisUtils.setCacheObject(IDPCacheConstants.CAS_CACHE_PREFIX + signedPrincipal.getInstId(), list4, Duration.ofDays(7));
        }
    }*/

    private String getSignatureSecret(Long appId,String type){
        String secret = "";
        type=type.toLowerCase();
        try {
            if (type.equals("des")) {
                secret = ReciprocalUtils.generateKey(ReciprocalUtils.Algorithm.DES);
            } else if (type.equals("desede")) {
                secret = ReciprocalUtils.generateKey(ReciprocalUtils.Algorithm.DESede);
            } else if (type.equals("aes")) {
                secret = ReciprocalUtils.generateKey(ReciprocalUtils.Algorithm.AES);
            } else if (type.equals("blowfish")) {
                secret = ReciprocalUtils.generateKey(ReciprocalUtils.Algorithm.Blowfish);
            } else if (type.equalsIgnoreCase("RS256")
                    || type.equalsIgnoreCase("RS384")
                    || type.equalsIgnoreCase("RS512")) {
                RSAKey rsaJWK = new RSAKeyGenerator(2048)
                        .keyID(appId + "_sig")
                        .keyUse(KeyUse.SIGNATURE)
                        .algorithm(new JWSAlgorithm(type.toUpperCase(), Requirement.OPTIONAL))
                        .generate();
                secret = rsaJWK.toJSONString();
            } else if (type.equalsIgnoreCase("HS256")
                    || type.equalsIgnoreCase("HS384")
                    || type.equalsIgnoreCase("HS512")) {
                OctetSequenceKey octKey = new OctetSequenceKeyGenerator(2048)
                        .keyID(appId + "_sig")
                        .keyUse(KeyUse.SIGNATURE)
                        .algorithm(new JWSAlgorithm(type.toUpperCase(), Requirement.OPTIONAL))
                        .generate();
                secret = octKey.toJSONString();
            } else if (type.equalsIgnoreCase("RSA1_5")
                    || type.equalsIgnoreCase("RSA_OAEP")
                    || type.equalsIgnoreCase("RSA-OAEP-256")) {
                RSAKey rsaJWK = new RSAKeyGenerator(2048)
                        .keyID(appId + "_enc")
                        .keyUse(KeyUse.ENCRYPTION)
                        .algorithm(new JWEAlgorithm(type.toUpperCase(), Requirement.OPTIONAL))
                        .generate();
                secret = rsaJWK.toJSONString();
            } else if (type.equalsIgnoreCase("A128KW")
                    || type.equalsIgnoreCase("A192KW")
                    || type.equalsIgnoreCase("A256KW")
                    || type.equalsIgnoreCase("A128GCMKW")
                    || type.equalsIgnoreCase("A192GCMKW")
                    || type.equalsIgnoreCase("A256GCMKW")) {
                int keyLength = Integer.parseInt(type.substring(1, 4));
                OctetSequenceKey octKey = new OctetSequenceKeyGenerator(keyLength)
                        .keyID(appId + "_enc")
                        .keyUse(KeyUse.ENCRYPTION)
                        .algorithm(new JWEAlgorithm(type.toUpperCase(), Requirement.OPTIONAL))
                        .generate();
                secret = octKey.toJSONString();
            } else {
                secret = ReciprocalUtils.generateKey("");
            }
        }catch (Exception e){
            log.error("error",e);
        }
        return secret;
    }

}
