package com.ldapauth.autoconfigure;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import com.ldapauth.crypto.password.*;
import com.ldapauth.persistence.service.PolicyLoginService;
import com.ldapauth.persistence.service.PolicyPasswordService;
import com.ldapauth.util.IdGenerator;
import com.ldapauth.util.Instance;
import com.ldapauth.util.SnowFlakeId;
import com.ldapauth.web.WebContext;
import com.ldapauth.constants.ConstsPersistence;
import com.ldapauth.cache.InMemoryMomentaryService;
import com.ldapauth.cache.MomentaryService;
import com.ldapauth.cache.RedisMomentaryService;
import com.ldapauth.persistence.repository.PolicyRepository;
import com.ldapauth.persistence.repository.PasswordPolicyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

@AutoConfiguration
public class ApplicationAutoConfiguration  implements InitializingBean {
	static final Logger _logger = LoggerFactory.getLogger(ApplicationAutoConfiguration.class);

    @Bean
    public PasswordReciprocal passwordReciprocal() {
        return new PasswordReciprocal();
    }


    /**
     * Authentication Password Encoder .
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String ,PasswordEncoder > encoders = new HashMap<>();
        encoders.put("bcrypt", (PasswordEncoder) Instance.newInstance(BCryptPasswordEncoder.class));
        encoders.put("sm3", (PasswordEncoder) Instance.newInstance(SM3PasswordEncoder.class));
        encoders.put("scrypt", (PasswordEncoder) Instance.newInstance(SCryptPasswordEncoder.class));
        encoders.put("md4", (PasswordEncoder) Instance.newInstance(Md4PasswordEncoder.class));
        encoders.put("md5", (PasswordEncoder) Instance.newInstance(MessageDigestPasswordEncoder.class,new Object[]{"MD5"}));
        encoders.put("ldap", (PasswordEncoder) Instance.newInstance(LdapShaPasswordEncoder.class));
        encoders.put("pbkdf2", (PasswordEncoder) Instance.newInstance(Pbkdf2PasswordEncoder.class));
        encoders.put("plain", (PasswordEncoder) Instance.newInstance(NoOpPasswordEncoder.class));
        String defaultidForEncode = "bcrypt";
        //idForEncode is default for encoder
        PasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(defaultidForEncode, encoders);
        _logger.debug("{} is default encoder" , defaultidForEncode);
        return passwordEncoder;
    }

    @Bean
    public PolicyRepository passwordPolicyRepository(
            PolicyLoginService policyLoginService,
            PolicyPasswordService policyPasswordService) {
        return new PolicyRepository(policyLoginService,policyPasswordService);
    }


    @Bean
    public PasswordPolicyValidator passwordPolicyValidator(
            PolicyLoginService policyLoginService,
            PolicyPasswordService policyPasswordService,
            MessageSource messageSource) {

        return new PasswordPolicyValidator(policyLoginService,policyPasswordService,messageSource);
    }

    /**
     * Id Generator .
     * @return
     */
    @Bean
    public IdGenerator idGenerator(
            @Value("${ldapauth.id.strategy:SnowFlake}") String strategy,
            @Value("${ldapauth.id.datacenterId:0}") int datacenterId,
            @Value("${ldapauth.id.machineId:0}") int machineId) {
    	IdGenerator idGenerator = new IdGenerator(strategy);
    	SnowFlakeId snowFlakeId = new SnowFlakeId(datacenterId,machineId);
    	idGenerator.setSnowFlakeId(snowFlakeId);
    	WebContext.idGenerator = idGenerator;
        return idGenerator;
    }

    @Bean
    public MomentaryService momentaryService(

    		@Value("${ldapauth.server.persistence}") int persistence) throws JOSEException {
    	MomentaryService momentaryService;
    	if (persistence == ConstsPersistence.REDIS) {
    		momentaryService = new RedisMomentaryService();
    	}else {
    		momentaryService = new InMemoryMomentaryService();
    	}
    	return momentaryService;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
