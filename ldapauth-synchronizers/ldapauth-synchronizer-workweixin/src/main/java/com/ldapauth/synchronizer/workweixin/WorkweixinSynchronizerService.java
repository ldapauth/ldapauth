package com.ldapauth.synchronizer.workweixin;

import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.synchronizer.ISynchronizerService;
import com.ldapauth.synchronizer.workweixin.pull.WorkweixinPullService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WorkweixinSynchronizerService implements ISynchronizerService {
	Synchronizers synchronizer;

	@Autowired
	WorkweixinPullService workweixinPullService;

	public WorkweixinSynchronizerService() {
		super();
	}

	public void sync() {
		log.info("Sync ...");
		workweixinPullService.setSynchronizer(synchronizer);
		workweixinPullService.sync();
	}

	@Override
	public void setSynchronizer(Synchronizers synchronizer) {
		this.synchronizer = synchronizer;
	}

}
