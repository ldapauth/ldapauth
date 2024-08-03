package com.ldapauth.synchronizer.ldap;

import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.synchronizer.ISynchronizerService;
import com.ldapauth.synchronizer.ldap.pull.LdapPullService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LdapSynchronizerService implements ISynchronizerService {
	Synchronizers synchronizer;
	@Autowired
	LdapPullService ldapPullService;

	public LdapSynchronizerService() {
		super();
	}

	public void sync() {
		log.info("ldap Sync ...");

		ldapPullService.setSynchronizer(synchronizer);
		ldapPullService.sync();

	}
	@Override
	public void setSynchronizer(Synchronizers synchronizer) {
		this.synchronizer = synchronizer;
	}

}
