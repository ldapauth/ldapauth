package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.entity.SocialsAssociate;
import me.zhyd.oauth.model.AuthUser;

/**
 * @author Shi.bl
 *
 */
public interface SocialsAssociateService extends IService<SocialsAssociate> {


    /**
     * 根据第三方用户和第三方标识查询第三方用户
     * @param socialsId
     * @param socialsUserId
     * @return
     */
    SocialsAssociate getObjectBySocialsUserId(Long socialsId,String socialsUserId);

    /**
     * 绑定系统用户和第三方
     * @param userId 系统用户标识
     * @param authUser 第三方用户对象
     * @return
     */
    boolean bindUser(Long userId, AuthUser authUser);
}
