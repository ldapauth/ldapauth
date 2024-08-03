package com.ldapauth.synchronizer.dingtalk;

import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.synchronizer.ISynchronizerService;
import com.ldapauth.synchronizer.dingtalk.pull.DingtalkPullService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DingtalkSynchronizerService implements ISynchronizerService {
	Synchronizers synchronizer;

	@Autowired
	DingtalkPullService dingtalkPullService;

	public DingtalkSynchronizerService() {
		super();
	}

	public void sync() {
		log.info("Dingtalk Sync ...");
		dingtalkPullService.setSynchronizer(synchronizer);
		dingtalkPullService.sync();
	}

	@Override
	public void setSynchronizer(Synchronizers synchronizer) {
		this.synchronizer = synchronizer;
	}

}
