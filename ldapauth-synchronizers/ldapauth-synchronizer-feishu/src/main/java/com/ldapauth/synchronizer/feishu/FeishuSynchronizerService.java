package com.ldapauth.synchronizer.feishu;

import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.synchronizer.ISynchronizerService;
import com.ldapauth.synchronizer.feishu.pull.FeishuPullService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeishuSynchronizerService implements ISynchronizerService {
	Synchronizers synchronizer;
	@Autowired
	FeishuPullService feishuPullService;



	public FeishuSynchronizerService() {
		super();
	}

	public void sync() {
		log.info("feishu Sync ...");
		feishuPullService.setSynchronizer(synchronizer);
		feishuPullService.sync();
	}

	@Override
	public void setSynchronizer(Synchronizers synchronizer) {
		this.synchronizer = synchronizer;
	}

}
