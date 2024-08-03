package com.ldapauth.provision;

import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.pojo.entity.Group;
import com.ldapauth.pojo.entity.Organization;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.provision.thread.LdapSyncThread;
import com.ldapauth.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ProvisionService {

    @Autowired
    SynchronizersService synchronizersService;

    /**
     * send  msg
     * @param topic  TOPIC
     * @param content msg Object
     * @param actionType CREATE UPDATE DELETE
     */
    public void send(String topic,Object content,String actionType) {
        if (cheackLdap(content)) {
            ProvisionMessage message =
                    new ProvisionMessage(
                            UUID.randomUUID().toString(),    //message id as uuid
                            topic,    //TOPIC
                            actionType,    //action of content
                            DateUtils.getCurrentDateTimeAsString(),    //send time
                            content    //content Object to json message content
                    );
            //线程池执行同步
            push(synchronizersService, topic, message);
        }
    }
    /**
     * 定义200个线程池
     */

    private void push(SynchronizersService synchronizersService,
                     String topic, ProvisionMessage provisionMessage){
        LdapSyncThread restSyncThread =  new LdapSyncThread(synchronizersService,topic,provisionMessage);
        restSyncThread.run();
    }


    /**
     * 检查发送对象的来源是否本地
     * @param content
     * @return
     */
    private boolean cheackLdap(Object content){
        if(content instanceof UserInfo) {
            UserInfo info = (UserInfo) content;
            if (StringUtils.isNotEmpty(info.getObjectFrom()) && info.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
                log.debug("data from {},not ldap not send ...",info.getObjectFrom());
                return false;
            }
        }
        else if(content instanceof Group) {
            Group info = (Group) content;
            if (StringUtils.isNotEmpty(info.getObjectFrom()) && info.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
                log.debug("data from {},not ldap not send ...",info.getObjectFrom());
                return false;
            }
        }
        else if(content instanceof Organization) {
            Organization info = (Organization) content;
            if (StringUtils.isNotEmpty(info.getObjectFrom()) &&  info.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
                log.debug("data from {},not ldap not send ...",info.getObjectFrom());
                return false;
            }
        } else {
            log.debug("Source unknown not send ...");
            return false;
        }
        return true;
    }
}
