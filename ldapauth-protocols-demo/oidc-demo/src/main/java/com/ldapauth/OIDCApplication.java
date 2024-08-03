package com.ldapauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import java.net.InetAddress;

/**
 * 程序启动入口
 * @author shi.bl
 *
 */
@SpringBootApplication
@Slf4j
public class OIDCApplication extends SpringBootServletInitializer {

    /**
     * @param args start parameter
     */
    public static void main(String[] args) {
        try {

            log.info("oauth demo Start Application ...");
            ConfigurableApplicationContext application = SpringApplication.run(OIDCApplication.class, args);
            Environment env = application.getEnvironment();
            log.info("\n----------------------------------------------------------\n\t" +
                            "Application '{}' is running! Access URLs:\n\t" +
                            "Local: \t\thttp://localhost:{}\n\t" +
                            "External: \thttp://{}:{}\n\t"+
                            "----------------------------------------------------------",
                    env.getProperty("spring.application.name"),
                    env.getProperty("server.port"),
                    InetAddress.getLocalHost().getHostAddress(),
                    env.getProperty("server.port"));
            log.info("started success.");
        }catch (Exception e){
            log.error("error:{}",e);
        }
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OIDCApplication.class);
    }
}
