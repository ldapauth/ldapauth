package com.ldapauth.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.persistence.mapper.SmsProviderMapper;
import com.ldapauth.persistence.service.SmsProviderService;
import com.ldapauth.pojo.dto.IdsListDto;
import com.ldapauth.pojo.entity.SmsProvider;
import com.ldapauth.pojo.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

import static com.ldapauth.constants.ConstsStatus.*;


/**
 * @description:
 * @author: orangeBabu
 * @time: 15/7/2024 AM9:49
 */

@Service
public class SmsProviderServiceImpl extends ServiceImpl<SmsProviderMapper, SmsProvider> implements SmsProviderService{

    @Autowired
    CacheService cacheService;

    @Override
    @Transactional
    public Result<String> add(SmsProvider smsProvider) {
        //停用其他提供商
        disableSmsProvider();

        boolean result = super.save(smsProvider);

        if (result) {
            cacheService.setCacheObject(ConstsCacheData.CACHE_NAME_SMS_PROVIDER,smsProvider);
            return Result.success("新增成功");
        }
        return Result.failed("新增失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> edit(SmsProvider smsProvider) {
        boolean result = super.updateById(smsProvider);
        if (result) {
            if (ENABLE_STATUS.equals(smsProvider.getStatus())) {
                cacheService.setCacheObject(ConstsCacheData.CACHE_NAME_SMS_PROVIDER,smsProvider);
            }

            return Result.success("修改成功");
        }

        return Result.failed("修改失败");
    }

    private void disableSmsProvider() {
        UpdateWrapper<SmsProvider> queryWrapper = new UpdateWrapper<>();
        queryWrapper.lambda().set(SmsProvider::getStatus, DISABLE_STATUS)
                .eq(SmsProvider::getStatus, ENABLE_STATUS);
        super.update(queryWrapper);
    }

    @Override
    public Result<SmsProvider> get(Long id) {
        return Result.success(super.getById(id));
    }

    /**
     * @Description: 写入缓存
     */
    @PostConstruct
    public void initSmsProvider() {
        LambdaQueryWrapper<SmsProvider> queryWrapper = new LambdaQueryWrapper<SmsProvider>()
                .eq(SmsProvider::getStatus, 0)
                .orderByDesc(SmsProvider::getUpdateTime).last("limit 1");
        SmsProvider smsProvider = super.getOne(queryWrapper);
        if (Objects.nonNull(smsProvider)) {
            cacheService.setCacheObject(ConstsCacheData.CACHE_NAME_SMS_PROVIDER, smsProvider);
        }
    }

    @Override
    public SmsProvider getCurrentSmsProvider() {
        //逻辑上限制默认只能有一个启用
        SmsProvider smsProvider = cacheService.getCacheObject(ConstsCacheData.CACHE_NAME_SMS_PROVIDER);
        if (Objects.isNull(smsProvider)) {
            LambdaQueryWrapper<SmsProvider> queryWrapper = new LambdaQueryWrapper<SmsProvider>()
                    .eq(SmsProvider::getStatus, 0)
                    .orderByDesc(SmsProvider::getUpdateTime).last("limit 1");
            smsProvider = super.getOne(queryWrapper);
        }

        return smsProvider;
    }

    @Override
    public Result<String> delete(IdsListDto idsListDto) {
        List<Long> ids = idsListDto.getIds();
        boolean result = super.removeBatchByIds(ids);
        if (result) {
            SmsProvider smsProvider = cacheService.getCacheObject(ConstsCacheData.CACHE_NAME_SMS_PROVIDER);
            if (Objects.nonNull(smsProvider) && ids.contains(smsProvider.getId())) {
                cacheService.deleteObject(ConstsCacheData.CACHE_NAME_SMS_PROVIDER);
            }
            return Result.success("删除成功");
        }
        return Result.failed("删除失败");
    }

    @Override
    public Result<String> disable(Long id) {
        boolean result = super.update(Wrappers.<SmsProvider>lambdaUpdate()
                .set(SmsProvider::getStatus, DISABLE_STATUS)
                .eq(SmsProvider::getId, id));
        if (result) {
            cacheService.deleteObject(ConstsCacheData.CACHE_NAME_SMS_PROVIDER);
            return Result.success("禁用成功");
        }
        return Result.failed("禁用失败");
    }

    @Override
    public Result<String> active(Long id) {
        disableSmsProvider();

        boolean result = super.update(Wrappers.<SmsProvider>lambdaUpdate()
                .set(SmsProvider::getStatus, ENABLE_STATUS)
                .eq(SmsProvider::getId, id));
        if (result) {
            cacheService.setCacheObject(ConstsCacheData.CACHE_NAME_SMS_PROVIDER, super.getById(id));
            return Result.success("启用成功");
        }
        return Result.failed("启用失败");
    }
}
