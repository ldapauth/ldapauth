package com.ldapauth.provision.abstracts;

import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.persistence.service.PolicyPasswordService;
import com.ldapauth.pojo.entity.PolicyPassword;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.util.LdapUtils;
import com.ldapauth.utils.PasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import javax.naming.directory.DirContext;
import java.util.Objects;

@Slf4j
public abstract class AbstractBaseHandle {

    /**
     * 获取连接池
     * @param synchronizer
     * @return
     */
    public DirContext getDirContext(Synchronizers synchronizer){
        DirContext dirContext = null;
        try {
            LdapUtils utils = new LdapUtils(
                    synchronizer.getBaseApi(),
                    synchronizer.getClientId(),
                    synchronizer.getClientSecret(),
                    synchronizer.getBaseDn());
            dirContext = utils.openConnection();
        }catch (Exception e){
            log.error("error",e);
        }
        return dirContext;
    }


    /**
     * 获取用户DN
     * @param objectFrom
     * @param uid
     * @param synchronizer
     * @return
     */
    public String getLdapUserDN(String objectFrom,String uid,Synchronizers synchronizer){
        String userDn = "uid="+uid+","+synchronizer.getBaseDn();
        switch (objectFrom) {
            case ConstsSynchronizers.OPEN_LDAP:
                userDn = "uid="+uid+","+synchronizer.getUserDn();
                break;
            case ConstsSynchronizers.DINGDING:
                userDn = "uid="+uid+",ou=people,ou=钉钉根节点,"+synchronizer.getBaseDn();
                break;
            case ConstsSynchronizers.FEISHU:
                userDn = "uid="+uid+",ou=people,ou=飞书根节点,"+synchronizer.getBaseDn();
                break;
            case ConstsSynchronizers.WORKWEIXIN:
                userDn = "uid="+uid+",ou=people,ou=企业微信根节点,"+synchronizer.getBaseDn();
                break;
        }
        return userDn;
    }



}
