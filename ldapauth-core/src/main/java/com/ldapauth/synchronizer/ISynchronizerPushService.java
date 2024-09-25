
package com.ldapauth.synchronizer;

import com.ldapauth.pojo.entity.Synchronizers;

/**
 * 同步推送接口规范
 */
public interface ISynchronizerPushService {

    /**
     * 执行创建
     * @param synchronizer 同步配置
     * @param data 同步对象
     * @return
     */
    boolean add(Synchronizers synchronizer,Object data);
    /**
     * 执行修改
     * @param synchronizer 同步配置
     * @param data 同步对象
     * @return
     */
    boolean update(Synchronizers synchronizer,Object data);

    /**
     * 执行删除
     * @param synchronizer 同步配置
     * @param data 同步对象
     * @return
     */
    boolean delete(Synchronizers synchronizer,Object data);

    /**
     * 密码修改
     * @param synchronizer 同步配置
     * @param data 同步对象
     * @return
     */
    boolean password(Synchronizers synchronizer,Object data);


}
