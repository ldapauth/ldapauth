package com.ldapauth.synchronizer;

import com.ldapauth.pojo.entity.Synchronizers;

/**
 * 同步接口定义
 */
public interface ISynchronizerService {

	void sync() ;

	void setSynchronizer(Synchronizers synchronizer);
}
