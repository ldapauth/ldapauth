package com.ldapauth.persistence.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.persistence.mapper.SystemsMapper;
import com.ldapauth.persistence.service.SystemsService;
import com.ldapauth.pojo.entity.Systems;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class SystemsServiceImpl extends ServiceImpl<SystemsMapper, Systems> implements SystemsService {

    @Autowired
    CacheService cacheService;


    @Override
    public Systems get() {
        Systems system = cacheService.getCacheObject(ConstsCacheData.SYSTEMS_KEY);
        if(Objects.nonNull(system)) {
            return system;
        } else {
            List<Systems> list = super.list();
            if (CollectionUtil.isNotEmpty(list)) {
                system = list.get(0);
            } else {
                system = new Systems();
                system.setId(1L);
                system.setCopyright("Copyright © 2024 ldapauth.com All Rights Reserved");
                system.setTitle("LdapAuth管理系统");
                system.setLogo("/src/assets/logo/logo.png");
                system.setCreateBy(0L);
                system.setCreateTime(new Date());
                system.setUpdateBy(0L);
                system.setUpdateTime(new Date());
                super.saveOrUpdate(system);
            }
            cacheService.setCacheObject(ConstsCacheData.SYSTEMS_KEY,system);
        }
        return system;
    }

    Object lock = new Object();

    @Override
    public boolean updateById(Systems entity) {
        boolean flag = super.updateById(entity);
        if(flag) {
            synchronized (lock) {
                entity = super.getById(entity.getId());
                cacheService.deleteObject(ConstsCacheData.SYSTEMS_KEY);
                cacheService.setCacheObject(ConstsCacheData.SYSTEMS_KEY,entity);
            }
        }
        return flag;
    }
}
