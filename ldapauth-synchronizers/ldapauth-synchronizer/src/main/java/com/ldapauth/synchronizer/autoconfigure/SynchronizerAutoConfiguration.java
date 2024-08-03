package com.ldapauth.synchronizer.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * 同步器自动装配
 */
@AutoConfiguration
public class SynchronizerAutoConfiguration implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {

	}
}
