package com.ldapauth.authn.support.rememberme;

import com.ldapauth.constants.ConstsPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class RemeberMeManagerFactory {
	private static final  Logger _logger =
            LoggerFactory.getLogger(RemeberMeManagerFactory.class);

	 public AbstractRemeberMeManager getService(
			 	int persistence,
			 	JdbcTemplate jdbcTemplate
	           ){

		 AbstractRemeberMeManager remeberMeService = null;
	        if (persistence == ConstsPersistence.INMEMORY) {
	            remeberMeService = new InMemoryRemeberMeManager();
	            _logger.debug("InMemoryRemeberMeService");
	        } else if (persistence == ConstsPersistence.JDBC) {
	            //remeberMeService = new JdbcRemeberMeService(jdbcTemplate);
	            _logger.debug("JdbcRemeberMeService not support ");
	        } else if (persistence == ConstsPersistence.REDIS) {
	            _logger.debug("RedisRemeberMeService  not support ");
	        }
	        return remeberMeService;
	}
}
