package com.ldapauth.autoconfigure;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;


@AutoConfiguration
public class OneTimePasswordAutoConfiguration  implements InitializingBean {
    private static final  Logger _logger =
            LoggerFactory.getLogger(OneTimePasswordAutoConfiguration.class);

   /* @Bean(name = "mailOtpAuthnService")
    public MailOtpAuthnService mailOtpAuthnService(
            @Value("${ldapauth.server.persistence}") int persistence,
            SmsProviderService smsProviderService,
            EmailSendersService emailSendersService,
            ProvisionService provisionService,
            RedisConnectionFactory redisConnFactory) {
        MailOtpAuthnService otpAuthnService =
        		new MailOtpAuthnService(
        				smsProviderService,
        				emailSendersService,
        				provisionService);

        if (persistence == ConstsPersistence.REDIS) {
            RedisOtpTokenStore redisOptTokenStore = new RedisOtpTokenStore(redisConnFactory);
            otpAuthnService.setRedisOptTokenStore(redisOptTokenStore);
        }

        _logger.debug("MailOtpAuthnService {} inited." ,
        				persistence == ConstsPersistence.REDIS ? "Redis" : "InMemory");
        return otpAuthnService;
    }*/


    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
