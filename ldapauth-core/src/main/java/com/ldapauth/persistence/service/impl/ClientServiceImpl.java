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
import com.ldapauth.persistence.service.ClientService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.client.Client;
import com.ldapauth.pojo.entity.client.details.ClientCASDetails;
import com.ldapauth.pojo.entity.client.details.ClientJWTDetails;
import com.ldapauth.pojo.entity.client.details.ClientOIDCDetails;
import com.ldapauth.pojo.entity.client.details.ClientSAMLDetails;
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
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {
    @Autowired
    ClientOIDCDetailsMapper appsOidcDetailsMapper;

    @Autowired
    ClientSAMLDetailsMapper appsSamlDetailsMapper;

    @Autowired
    ClientJWTDetailsMapper appsJwtDetailsMapper;

    @Autowired
    ClientCASDetailsMapper appsCasDetailsMapper;

    @Autowired
    ClientMapper mapper;

    @Autowired
    IdentifierGenerator identifierGenerator;

    @Autowired
    KeyStoreLoader keyStoreLoader;

    @Autowired
    CacheService cacheService;
    @Override
    public List<Client> myClient(Long userId) {
        return mapper.myClient(userId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    @Override
    public Result<String> oidcCreate(AppsOidcDTO dto) {
        //SignedPrincipal signedPrincipal = AuthorizationUtils.getCurrentUser();
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }

        Client app = BeanUtil.copyProperties(dto.getApp(), Client.class);
        //app.setInstId(signedPrincipal.getInstId());
        app.setProtocol(0);
        //生成clientId和密钥
        buildClient(app);
        encodeSecret(app);
        super.save(app);
        ClientOIDCDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientOIDCDetails.class);
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
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        //排除当前ID
        queryWrapper.notIn(Client::getId,dto.getApp().getId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        queryWrapper.notIn(Client::getId,dto.getApp().getId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }



        Client app = BeanUtil.copyProperties(dto.getApp(), Client.class);
        app.setProtocol(0);
        app.setClientSecret(dto.getDetails().getClientSecret());
        encodeSecret(app);

        super.updateById(app);
        ClientOIDCDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientOIDCDetails.class) ;
        details.setAppId(app.getId());
        details.setStatus(app.getStatus());
        details.setClientSecret(app.getClientSecret());
        //details.setInstId(signedPrincipal.getInstId());

        LambdaQueryWrapper<ClientOIDCDetails> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ClientOIDCDetails::getAppId,app.getId());
        //updateWrapper.eq(AppsOAuth2ClientDetails::getInstId,app.getInstId());

        ClientOIDCDetails oidcDetails = appsOidcDetailsMapper.selectOne(updateWrapper);
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


    private void encodeSecret(Client app){
        if(StringUtils.isNotBlank(app.getClientSecret())){
            String encodeSecret = PasswordReciprocal.getInstance().encode(app.getClientSecret());
            app.setClientSecret(encodeSecret);
        }
    }

    private void decoderSecret(Client app){
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
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }
        Client app = BeanUtil.copyProperties(dto.getApp(), Client.class);
        //app.setInstId(signedPrincipal.getInstId());
        app.setProtocol(1);
        //生成clientId和密钥
        buildClient(app);
        encodeSecret(app);
        super.save(app);
        ClientSAMLDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientSAMLDetails.class);
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
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        //排除当前ID
        queryWrapper.notIn(Client::getId,dto.getApp().getId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(Client::getId,dto.getApp().getId());
        queryWrapper.eq(Client::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }
        Client app = BeanUtil.copyProperties(dto.getApp(), Client.class);
        app.setProtocol(1);
        encodeSecret(app);
        super.updateById(app);
        ClientSAMLDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientSAMLDetails.class);

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

        LambdaQueryWrapper<ClientSAMLDetails> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ClientSAMLDetails::getAppId,app.getId());
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
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }
        Client app = BeanUtil.copyProperties(dto.getApp(), Client.class);
        //app.setInstId(signedPrincipal.getInstId());
        app.setProtocol(2);
        //生成clientId和密钥
        buildClient(app);
        encodeSecret(app);
        super.save(app);
        ClientJWTDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientJWTDetails.class) ;
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
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        //排除当前ID
        queryWrapper.notIn(Client::getId,dto.getApp().getId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(Client::getId,dto.getApp().getId());
        queryWrapper.eq(Client::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }
        Client app = BeanUtil.copyProperties(dto.getApp(), Client.class);
        app.setProtocol(2);
        encodeSecret(app);
        super.updateById(app);
        ClientJWTDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientJWTDetails.class) ;
        details.setAppId(app.getId());
        //details.setInstId(signedPrincipal.getInstId());
        details.setStatus(app.getStatus());

        LambdaQueryWrapper<ClientJWTDetails> selectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        selectLambdaQueryWrapper.eq(ClientJWTDetails::getAppId,app.getId());
        //selectLambdaQueryWrapper.eq(AppsJwtDetails::getInstId,app.getInstId());
        ClientJWTDetails jwtDetails = appsJwtDetailsMapper.selectOne(selectLambdaQueryWrapper);
        //存在签名
         if(details.getIsSignature().equalsIgnoreCase(IDPCacheConstants.YES)) {
            //更新时判断之前的算法和更新的算法是否一直，一直则不更新签名密钥
            if(!jwtDetails.getSignature().equalsIgnoreCase(details.getSignature())) {
                details.setSignatureKey(getSignatureSecret(app.getId(), details.getSignature()));
            }
        } else {
            details.setSignatureKey("");
        }
        LambdaQueryWrapper<ClientJWTDetails> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ClientJWTDetails::getAppId,app.getId());
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
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppName,dto.getApp().getAppName());
       // queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppCode,dto.getApp().getAppCode());
       // queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }

        Client app = BeanUtil.copyProperties(dto.getApp(), Client.class);
        //app.setInstId(signedPrincipal.getInstId());
        app.setProtocol(3);
        //生成clientId和密钥
        buildClient(app);
        encodeSecret(app);
        super.save(app);
        ClientCASDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientCASDetails.class) ;
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
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getAppName,dto.getApp().getAppName());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        //排除当前ID
        queryWrapper.notIn(Client::getId,dto.getApp().getId());
        List list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用名称已重复");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(Client::getId,dto.getApp().getId());
        queryWrapper.eq(Client::getAppCode,dto.getApp().getAppCode());
        //queryWrapper.eq(Apps::getInstId,signedPrincipal.getInstId());
        list = this.list(queryWrapper);
        if (Objects.nonNull(list) && !list.isEmpty() && list.size()>0) {
            return Result.failed("应用编码已重复");
        }
        Client app = BeanUtil.copyProperties(dto.getApp(), Client.class);
        app.setProtocol(3);
        encodeSecret(app);
        super.updateById(app);
        ClientCASDetails details = BeanUtil.copyProperties(dto.getDetails(), ClientCASDetails.class) ;
        details.setAppId(app.getId());
        details.setStatus(app.getStatus());
        //details.setInstId(signedPrincipal.getInstId());
        LambdaQueryWrapper<ClientCASDetails> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(ClientCASDetails::getAppId,app.getId());
        //updateWrapper.eq(AppsCasDetails::getInstId,app.getInstId());
        appsCasDetailsMapper.update(details,updateWrapper);
        cacheService.deleteObject(ConstsCacheData.CAS_CACGE_LIST);
       // loadCasAllCache();
        return Result.success("修改成功");
    }

    @Override
    public Result<AppsDetails> getDetails(Long id) {
        Client app = super.getById(id);
        if (Objects.isNull(app)) {
            return Result.failed("查询失败");
        }
        decoderSecret(app);
        //获取oidc扩展配置
        if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 0) {
            AppsDetails<ClientOIDCDetails> details = new AppsDetails();
            details.setApp(app);
            LambdaQueryWrapper<ClientOIDCDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientOIDCDetails::getAppId,id);
            //queryWrapper.eq(AppsOAuth2ClientDetails::getInstId,app.getInstId());
            ClientOIDCDetails oauthDetails = appsOidcDetailsMapper.selectOne(queryWrapper);
            if (Objects.nonNull(oauthDetails)) {
                oauthDetails.setClientSecret(app.getClientSecret());
                details.setDetails(oauthDetails);
            }
            return Result.success(details);
        }
        //获取saml扩展配置
        else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 1) {
            AppsDetails<ClientSAMLDetails> details = new AppsDetails();
            details.setApp(app);
            LambdaQueryWrapper<ClientSAMLDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientSAMLDetails::getAppId,id);
            //queryWrapper.eq(AppsSamlDetails::getInstId,app.getInstId());
            ClientSAMLDetails samlDetails = appsSamlDetailsMapper.selectOne(queryWrapper);
            if (Objects.nonNull(samlDetails)) {
                details.setDetails(samlDetails);
            }
            return Result.success(details);
        }
        //获取jwt扩展配置
        else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 2) {
            AppsDetails<ClientJWTDetails> details = new AppsDetails();
            details.setApp(app);
            LambdaQueryWrapper<ClientJWTDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientJWTDetails::getAppId,id);
            //queryWrapper.eq(AppsJwtDetails::getInstId,app.getInstId());
            ClientJWTDetails jwtDetails = appsJwtDetailsMapper.selectOne(queryWrapper);
            if (Objects.nonNull(jwtDetails)) {
                details.setDetails(jwtDetails);
            }
            return Result.success(details);
        }
        //获取cas扩展配置
        else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 3) {
            AppsDetails<ClientCASDetails> details = new AppsDetails();
            details.setApp(app);
            LambdaQueryWrapper<ClientCASDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientCASDetails::getAppId,id);
            //queryWrapper.eq(AppsCasDetails::getInstId,app.getInstId());
            ClientCASDetails casdetails = appsCasDetailsMapper.selectOne(queryWrapper);
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
    public AppsDetails<ClientOIDCDetails> getDetailsClientId(String clientId) {
        LambdaQueryWrapper<Client> appQuery = new LambdaQueryWrapper();
        appQuery.eq(Client::getClientId,clientId);
        Client app = super.getOne(appQuery);
        if (Objects.isNull(app)) {
             appQuery = new LambdaQueryWrapper();
             appQuery.eq(Client::getId,clientId);
             app = super.getOne(appQuery);
        }
        if (Objects.isNull(app)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "无法获取应用对象");
        }
        Long id = app.getId();
        //获取oidc扩展配置
        if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 0) {
            AppsDetails<ClientOIDCDetails> details = new AppsDetails();
            details.setApp(app);
            LambdaQueryWrapper<ClientOIDCDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientOIDCDetails::getAppId,id);
            ClientOIDCDetails oauthDetails = appsOidcDetailsMapper.selectOne(queryWrapper);
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
        int count = mapper.update(new LambdaUpdateWrapper<Client>().in(Client::getId,ids).set(Client::getStatus,i));
        if (count > 0) {
            for(Long id : ids) {
                Client app = super.getById(id);
                //更新oauth扩展配置
                if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 0) {
                    LambdaQueryWrapper<ClientOIDCDetails> updateWrapper = new LambdaQueryWrapper<>();
                    updateWrapper.eq(ClientOIDCDetails::getAppId,id);
                    //updateWrapper.eq(AppsOAuth2ClientDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                    ClientOIDCDetails details = new ClientOIDCDetails();
                    details.setStatus(i);
                    appsOidcDetailsMapper.update(details,updateWrapper);
                    //details = appsOAuth2ClientDetailsMapper.selectOne(updateWrapper);
                    //details.setDeviceType(app.getDeviceType());
                    //RedisUtils.setCacheObject(IDPCacheConstants.OAUTH_CACHE_PREFIX+app.getClientId(),details,Duration.ofDays(7));
                }
                //更新saml扩展配置
                else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 1) {
                    LambdaQueryWrapper<ClientSAMLDetails> updateWrapper = new LambdaQueryWrapper<>();
                    updateWrapper.eq(ClientSAMLDetails::getAppId,id);
                    //updateWrapper.eq(AppsSamlDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                    ClientSAMLDetails details = new ClientSAMLDetails();
                    details.setStatus(i);
                    appsSamlDetailsMapper.update(details,updateWrapper);
                    //details = appsSamlDetailsMapper.selectOne(updateWrapper);
                    //details.setDeviceType(app.getDeviceType());
                    //String value = ObjectTransformer.serialize(details);
                    //RedisUtils.setCacheObject(IDPCacheConstants.SAML_CACHE_PREFIX + id,value,Duration.ofDays(7));
                }
                //更新jwt扩展配置
                else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 2) {
                    LambdaQueryWrapper<ClientJWTDetails> updateWrapper = new LambdaQueryWrapper<>();
                    updateWrapper.eq(ClientJWTDetails::getAppId,id);
                    //updateWrapper.eq(AppsJwtDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                    ClientJWTDetails details = new ClientJWTDetails();
                    details.setStatus(i);
                    appsJwtDetailsMapper.update(details,updateWrapper);
                    //details = AppsJwtDetailsMapper.selectOne(updateWrapper);
                    //details.setDeviceType(app.getDeviceType());
                    //RedisUtils.setCacheObject(IDPCacheConstants.JWT_CACHE_PREFIX+app.getId(),details,Duration.ofDays(7));

                }
                //更新cas扩展配置
                else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 3) {
                    LambdaQueryWrapper<ClientCASDetails> updateWrapper = new LambdaQueryWrapper<>();
                    updateWrapper.eq(ClientCASDetails::getAppId,id);
                    //updateWrapper.eq(AppsCasDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                    ClientCASDetails details = new ClientCASDetails();
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
        List<Client> list = this.listByIds(ids);
        for(Client app :list) {
            //检查是否存在启用的应用
            if (app.getStatus().intValue() == 0) {
                throw new BusinessException(400,"请先禁用应用后在删除");
            }
        }
        for (Long id : ids) {
            Client app = super.getById(id);
            //更新oauth扩展配置
            if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 0) {
                LambdaQueryWrapper<ClientOIDCDetails> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ClientOIDCDetails::getAppId,id);
                //wrapper.eq(AppsOAuth2ClientDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                appsOidcDetailsMapper.delete(wrapper);
                //RedisUtils.deleteObject(IDPCacheConstants.OAUTH_CACHE_PREFIX+app.getClientId());
            }
            //更新saml扩展配置
            else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 1) {
                LambdaQueryWrapper<ClientSAMLDetails> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ClientSAMLDetails::getAppId,id);
                //wrapper.eq(AppsSamlDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                appsSamlDetailsMapper.delete(wrapper);
                //RedisUtils.deleteObject(IDPCacheConstants.SAML_CACHE_PREFIX + id);
            }
            //更新jwt扩展配置
            else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 2) {
                LambdaQueryWrapper<ClientJWTDetails> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ClientJWTDetails::getAppId,id);
                //wrapper.eq(AppsJwtDetails::getInstId,AuthorizationUtils.getCurrentUser().getInstId());
                appsJwtDetailsMapper.delete(wrapper);
                //RedisUtils.deleteObject(IDPCacheConstants.JWT_CACHE_PREFIX+app.getId());
            }
            //删除cas扩展配置
            else if (Objects.nonNull(app.getProtocol()) && app.getProtocol().intValue() == 3) {
                LambdaQueryWrapper<ClientCASDetails> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ClientCASDetails::getAppId,id);
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
    public ClientCASDetails getAppDetails(String service, boolean isMach) {
        List<ClientCASDetails> list =  cacheService.getCacheObject(ConstsCacheData.CAS_CACGE_LIST);
        if(Objects.isNull(list)) {
            list = appsCasDetailsMapper.selectList(new LambdaQueryWrapper<>());
            cacheService.setCacheObject(ConstsCacheData.CAS_CACGE_LIST,list);
        }
        if (CollectionUtil.isNotEmpty(list)) {
           for(ClientCASDetails details : list) {
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
    public ClientJWTDetails getAppsJwtDetails(Long appId) {
        LambdaQueryWrapper<ClientJWTDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientJWTDetails::getAppId,appId);
        return appsJwtDetailsMapper.selectOne(queryWrapper);
    }

    @Override
    public Client getByClientId(String clientId, boolean isMach) {
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getClientId,clientId);
        return super.getOne(queryWrapper);
    }

    private void buildClient(Client app){
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
