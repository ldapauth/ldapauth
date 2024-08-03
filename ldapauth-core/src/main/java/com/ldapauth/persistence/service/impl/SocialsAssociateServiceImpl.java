package com.ldapauth.persistence.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.persistence.mapper.SocialsAssociateMapper;
import com.ldapauth.persistence.service.SocialsAssociateService;
import com.ldapauth.pojo.entity.SocialsAssociate;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class SocialsAssociateServiceImpl extends ServiceImpl<SocialsAssociateMapper, SocialsAssociate> implements SocialsAssociateService {

    @Override
    public SocialsAssociate getObjectBySocialsUserId(Long socialsId, String socialsUserId) {
        LambdaQueryWrapper<SocialsAssociate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SocialsAssociate::getSocialsId,socialsId);
        lambdaQueryWrapper.eq(SocialsAssociate::getSocialsUserId,socialsUserId);
        return super.getOne(lambdaQueryWrapper);
    }

    @Override
    public boolean bindUser(Long userId, AuthUser authUser) {
        LambdaQueryWrapper<SocialsAssociate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SocialsAssociate::getUserId,userId);
        lambdaQueryWrapper.eq(SocialsAssociate::getSocialsId,Long.valueOf(authUser.getSource()));
        //判断当前系统用户是已经绑定其他第三方账号
        if (CollectionUtil.isNotEmpty(super.list(lambdaQueryWrapper))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "系统用户已经绑定第三方用户，无法再次绑定。");
        }
        SocialsAssociate socialsAssociate = new SocialsAssociate();
        socialsAssociate.setUserId(userId);
        socialsAssociate.setSocialsId(Long.valueOf(authUser.getSource()));
        socialsAssociate.setSocialsUserId(authUser.getUuid());
        socialsAssociate.setSocialsAvatar(authUser.getAvatar());
        socialsAssociate.setSocialsNickName(authUser.getNickname());
        socialsAssociate.setCreateBy(0L);
        socialsAssociate.setCreateTime(new Date());
        return super.save(socialsAssociate);
    }
}
