
package com.ldapauth.provision.interfaces;


import com.ldapauth.pojo.entity.Synchronizers;

public interface BaseHandle {

    /**
     * 执行创建
     * @param synchronizer
     * @return
     */
    boolean add(Synchronizers synchronizer, Object data);
    /**
     * 执行修改
     * @param synchronizer
     * @return
     */
    boolean update(Synchronizers synchronizer,Object data);

    /**
     * 执行删除
     * @param synchronizer
     * @return
     */
    boolean delete(Synchronizers synchronizer,Object data);

    /**
     * 密码修改
     * @param synchronizer
     * @return
     */
    boolean password(Synchronizers synchronizer,Object data);


}
