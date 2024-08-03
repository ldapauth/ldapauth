package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.ChangeStatusDTO;
import com.ldapauth.pojo.dto.ResourceQueryDTO;
import com.ldapauth.pojo.entity.Synchronizers;

import java.util.Date;
import java.util.List;


/**
 * @author Shi.bl
 *
 */
public interface SynchronizersService extends IService<Synchronizers> {

    List<Synchronizers> list();

    /**
     * 测试配置
     * @param synchronizers
     * @return
     */
    boolean test(Synchronizers synchronizers);

    /**
     * 立即同步
     * @param synchronizerId 任务ID
     * @return
     */
    boolean sync(Long synchronizerId);


    /**
     * 获取LDAP缓存配置
     * @return
     */
    Synchronizers LdapConfig();


}
