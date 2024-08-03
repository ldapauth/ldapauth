package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.CallbackDTO;
import com.ldapauth.pojo.dto.SocialsProviderQueryDTO;
import com.ldapauth.pojo.entity.SocialsProvider;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;

import java.util.List;

/**
 * @author Shi.bl
 *
 */
public interface SocialsProviderService extends IService<SocialsProvider> {


    /**
     * 生成ID
     * @return
     */
    String generate();
    /**
     * 分页查询方法
     * @param queryDTO 分页查询参数
     * @return
     */
    Page<SocialsProvider> fetch(SocialsProviderQueryDTO queryDTO);

    /**
     * 获取集合
     * @return
     */
    List<SocialsProvider> cacheList();


    /**
     * 获取第三方发起对象
     * @param id
     * @return
     */
    AuthRequest authorize(Long id);


    /**
     * 获取第三方授权用户
     * @param callbackDTO
     * @return
     */
    AuthUser authCallback(CallbackDTO callbackDTO);


}
