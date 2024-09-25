package com.ldapauth;

import javax.servlet.ServletException;

import cn.hutool.core.io.FileUtil;
import com.ldapauth.util.SslUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.web.InitializeContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * ldapauth后台管理系统应用启动入口
 * @author Crystal.sea
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class LdapAuthMgtApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		Runtime runtime = Runtime.getRuntime();
		// 初始堆大小
		long initialMemory = runtime.totalMemory();
		// 最大堆大小
		long maxMemory = runtime.maxMemory();
		// 已用堆大小
		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		log.info("Initial Memory (bytes): " + initialMemory);
		log.info("Max Memory (bytes): " + maxMemory);
		log.info("Used Memory (bytes): " + usedMemory);
	    log.info("Start ldapauthMgt Application ...");
		//设置ldaps ssl证书
		String trustStore= "/app/ldapauth/trustStore";
		String trustStorePassword = "changeit";
		//检查是否存在证书
		if (FileUtil.exist(trustStore)) {
			log.info("load ssl trustStore : {} ",trustStore);
			System.setProperty("javax.net.ssl.trustStore", trustStore);
			System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		}
		//设置ldap策略
		System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification","true");
		ConfigurableApplicationContext  applicationContext =
							SpringApplication.run(LdapAuthMgtApplication.class, args);
		InitializeContext initWebContext = new InitializeContext(applicationContext);
		try {
			initWebContext.init(null);
		} catch (ServletException e) {
			log.error("Exception ",e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		log.info("ldapauthMgt at {}" , new DateTime());
		log.info("ldapauthMgt Server Port {}"
				,applicationContext.getBean(ApplicationConfig.class).getPort());
		log.info("ldapauthMgt started.");
	}

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(LdapAuthMgtApplication.class);
	}

}
