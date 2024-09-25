package com.ldapauth.provision.thread;

import cn.hutool.extra.spring.SpringUtil;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.provision.ProvisionAction;
import com.ldapauth.provision.ProvisionMessage;
import com.ldapauth.provision.ProvisionTopic;
import com.ldapauth.synchronizer.ISynchronizerPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread for send message
 *
 */
@Slf4j
@Component
public class LdapSyncThread {
    SynchronizersService synchronizersService;
    String topic;

    ProvisionMessage provisionMessage;

    ExecutorService executorService = Executors.newFixedThreadPool(200);


    public void run(
            SynchronizersService synchronizersService,
            String topic,
            ProvisionMessage provisionMessage) {

        this.topic = topic;
        this.provisionMessage = provisionMessage;
        this.synchronizersService = synchronizersService;

        Synchronizers synchronizers = getLdapConfig();

        if (Objects.nonNull(synchronizers)){
            //多线程执行
            executorService.execute(() ->  synchronizerPush(synchronizers));
        }
        log.debug("send to Message Queue finished .");
    }

    private void synchronizerPush(Synchronizers connector){
        ISynchronizerPushService synchronizerPushService = getSynchronizerPushService(connector);
        if (Objects.isNull(synchronizerPushService)) {
            log.error("执行器不存在！");
            return;
        }
        if (this.provisionMessage.getActionType().equalsIgnoreCase(ProvisionAction.CREATE_ACTION)) {
            synchronizerPushService.add(connector,provisionMessage.getContent());
        } else if (this.provisionMessage.getActionType().equalsIgnoreCase(ProvisionAction.UPDATE_ACTION)) {
            synchronizerPushService.update(connector,provisionMessage.getContent());
        } else if (this.provisionMessage.getActionType().equalsIgnoreCase(ProvisionAction.DELETE_ACTION)) {
            synchronizerPushService.delete(connector,provisionMessage.getContent());
        } else if (this.provisionMessage.getActionType().equalsIgnoreCase(ProvisionAction.PASSWORD_ACTION)) {
            synchronizerPushService.password(connector,provisionMessage.getContent());
        }
    }

    /**
     * 获取执行器
     * @return
     */
    private ISynchronizerPushService getSynchronizerPushService(Synchronizers synchronizers){
        ISynchronizerPushService synchronizerPushService = null;
        //判断链接来源
        if (ConstsSynchronizers.OPEN_LDAP.equalsIgnoreCase(synchronizers.getClassify())){
            if (this.topic.equalsIgnoreCase(ProvisionTopic.ORG_TOPIC)) {
                synchronizerPushService = SpringUtil.getBean("ldapPushOrgsService");
            } else if (this.topic.equalsIgnoreCase(ProvisionTopic.USERINFO_TOPIC)) {
                synchronizerPushService = SpringUtil.getBean("ldapPushUsersService");
            } else if (this.topic.equalsIgnoreCase(ProvisionTopic.GROUP_TOPIC)) {
                synchronizerPushService = SpringUtil.getBean("ldapPushGroupService");
            }
        }
        //判断链接来源
        else if (ConstsSynchronizers.ACTIVEDIRECTORY.equalsIgnoreCase(synchronizers.getClassify())){
            if (this.topic.equalsIgnoreCase(ProvisionTopic.ORG_TOPIC)) {
                synchronizerPushService = SpringUtil.getBean("activedirectoryPushOrgsService");
            } else if (this.topic.equalsIgnoreCase(ProvisionTopic.USERINFO_TOPIC)) {
                synchronizerPushService = SpringUtil.getBean("activedirectoryPushUsersService");
            } else if (this.topic.equalsIgnoreCase(ProvisionTopic.GROUP_TOPIC)) {
                synchronizerPushService = SpringUtil.getBean("activedirectoryPushGroupService");
            }
        }
        return synchronizerPushService;
    }


    /**
     * 获取LDAP的连接器
     * @return
     */
    private Synchronizers getLdapConfig() {
        return synchronizersService.LdapConfig();
    }
}
