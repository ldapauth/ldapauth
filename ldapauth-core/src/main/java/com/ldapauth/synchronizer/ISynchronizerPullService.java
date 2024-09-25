package com.ldapauth.synchronizer;

import com.ldapauth.pojo.entity.Synchronizers;

/**
 * 同步拉去接口定义
 */
public interface ISynchronizerPullService {

	void sync();

	void setSynchronizer(Synchronizers synchronizer);
}
