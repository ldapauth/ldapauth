package com.ldapauth;

import javax.servlet.ServletException;
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

		System.out.println("Initial Memory (bytes): " + initialMemory);
		System.out.println("Max Memory (bytes): " + maxMemory);
		System.out.println("Used Memory (bytes): " + usedMemory);

	    log.info("Start ldapauthMgt Application ...");

		ConfigurableApplicationContext  applicationContext =
							SpringApplication.run(LdapAuthMgtApplication.class, args);
		InitializeContext initWebContext = new InitializeContext(applicationContext);
		try {
			initWebContext.init(null);
		} catch (ServletException e) {
			log.error("Exception ",e);
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
