package com.ldapauth.synchronizer.abstracts;

import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.util.LdapUtils;
import lombok.extern.slf4j.Slf4j;

import javax.naming.directory.DirContext;
import java.util.Objects;

/**
 * 定义抽象类，获取公共方法
 */
@Slf4j
public abstract class AbstractPushSynchronizer {



    /**
     * 获取连接池
     * @param synchronizer
     * @return
     */
    public DirContext getDirContext(Synchronizers synchronizer){
        DirContext dirContext = null;
        try {
            if (synchronizer.getBaseApi().startsWith("ldaps") &&
                    Objects.nonNull(synchronizer.getTrustStore()) &&
                    synchronizer.getOpenssl()) {
                LdapUtils utils = new LdapUtils(
                        synchronizer.getBaseApi(),
                        synchronizer.getClientId(),
                        synchronizer.getClientSecret(),
                        synchronizer.getBaseDn());
                utils.setSsl(true);
                utils.setTrustStore(synchronizer.getTrustStore());
                utils.setTrustStorePassword(synchronizer.getTrustStorePassword());
                dirContext = utils.openConnection();
            } else {
                LdapUtils utils = new LdapUtils(
                        synchronizer.getBaseApi(),
                        synchronizer.getClientId(),
                        synchronizer.getClientSecret(),
                        synchronizer.getBaseDn());
                dirContext = utils.openConnection();
            }
        }catch (Exception e){
            log.error("error",e);
        }
        return dirContext;
    }


    /**
     * 获取用户DN
     * @param objectFrom
     * @param subject 主体字段
     * @param value 值
     * @param synchronizer
     * @return
     */
    public String getLdapUserDN(String objectFrom,String subject,String value,Synchronizers synchronizer){
        String userDn = subject + "="+value+","+synchronizer.getBaseDn();
        switch (objectFrom) {
            case ConstsSynchronizers.OPEN_LDAP:
                userDn = subject + "="+value+","+synchronizer.getUserDn();
                break;
            case ConstsSynchronizers.ACTIVEDIRECTORY:
                userDn = subject + "="+value+","+synchronizer.getUserDn();
                break;
            case ConstsSynchronizers.DINGDING:
                userDn = subject + "="+value+",ou=people,ou=钉钉根节点,"+synchronizer.getBaseDn();
                break;
            case ConstsSynchronizers.FEISHU:
                userDn = subject + "="+value+",ou=people,ou=飞书根节点,"+synchronizer.getBaseDn();
                break;
            case ConstsSynchronizers.WORKWEIXIN:
                userDn = subject + "="+value+",ou=people,ou=企业微信根节点,"+synchronizer.getBaseDn();
                break;
        }
        return userDn;
    }




}
