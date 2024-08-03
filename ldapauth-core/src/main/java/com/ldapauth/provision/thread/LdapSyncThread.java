package com.ldapauth.provision.thread;

import cn.hutool.extra.spring.SpringUtil;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.provision.ProvisionAction;
import com.ldapauth.provision.ProvisionMessage;
import com.ldapauth.provision.ProvisionTopic;
import com.ldapauth.provision.interfaces.BaseHandle;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread for send message
 *
 */
@Slf4j
public class LdapSyncThread {
    SynchronizersService synchronizersService;
    String topic;

    ProvisionMessage provisionMessage;

    ExecutorService executorService = Executors.newFixedThreadPool(200);
    public LdapSyncThread(
            SynchronizersService synchronizersService,
            String topic,
            ProvisionMessage provisionMessage
    ) {
        this.topic = topic;
        this.provisionMessage = provisionMessage;
        this.synchronizersService = synchronizersService;
    }


    public void run() {
        Synchronizers synchronizers = getLdapConfig();
        //循环处理
        if (Objects.nonNull(synchronizers) && synchronizers.getStatus().intValue() == 0){
            //多线程执行
            executorService.execute(() ->  handle(synchronizers));
        }
        log.debug("send to Message Queue finished .");
    }

    private void handle(Synchronizers connector){
        BaseHandle baseHandle = getHandle();
        if (Objects.isNull(baseHandle)) {
            log.error("执行器不存在！");
            return;
        }
        if (this.provisionMessage.getActionType().equalsIgnoreCase(ProvisionAction.CREATE_ACTION)) {
            baseHandle.add(connector,provisionMessage.getContent());
        } else if (this.provisionMessage.getActionType().equalsIgnoreCase(ProvisionAction.UPDATE_ACTION)) {
            baseHandle.update(connector,provisionMessage.getContent());
        } else if (this.provisionMessage.getActionType().equalsIgnoreCase(ProvisionAction.DELETE_ACTION)) {
            baseHandle.delete(connector,provisionMessage.getContent());
        } else if (this.provisionMessage.getActionType().equalsIgnoreCase(ProvisionAction.PASSWORD_ACTION)) {
            baseHandle.password(connector,provisionMessage.getContent());
        }
    }

    /**
     * 获取执行器
     * @return
     */
    private BaseHandle getHandle(){
        BaseHandle baseHandle = null;
        if (this.topic.equalsIgnoreCase(ProvisionTopic.ORG_TOPIC)) {
            baseHandle = SpringUtil.getBean("ldapPushOrgsService");
        } else if (this.topic.equalsIgnoreCase(ProvisionTopic.USERINFO_TOPIC)) {
            baseHandle = SpringUtil.getBean("ldapPushUsersService");
        } else if (this.topic.equalsIgnoreCase(ProvisionTopic.GROUP_TOPIC)) {
            baseHandle = SpringUtil.getBean("ldapPushGroupService");
        }
        return baseHandle;
    }

    /**
     * 获取所有LDAP的连接器
     * @return
     */
    private Synchronizers getLdapConfig() {
        return synchronizersService.LdapConfig();
    }
}
