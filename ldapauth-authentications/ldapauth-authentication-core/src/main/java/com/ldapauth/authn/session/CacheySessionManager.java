package com.ldapauth.authn.session;

import java.time.LocalDateTime;
import com.ldapauth.cache.CacheService;
import com.ldapauth.configuration.AuthJwkConfig;
import com.ldapauth.util.ObjectTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


/**
 * 会话管理 内存存储
 *
 * @author Crystal.Sea
 *
 */
@Slf4j
public class CacheySessionManager implements SessionManager{

	CacheService cacheService;

	AuthJwkConfig authJwkConfig;
	/**
	 * 会话前缀key
	 */
	public static final String SESSION_PRX_KEY = "LDAPAUTH:SESSSION:ID:%s";


	public CacheySessionManager(CacheService cacheService,AuthJwkConfig authJwkConfig) {
        super();
		this.cacheService = cacheService;
		this.authJwkConfig = authJwkConfig;
    }

    @Override
	public void create(String sessionId, Session session) {
    	session.setExpiredTime(session.getLastAccessTime().plusSeconds(authJwkConfig.getRefreshExpires()));
		String serializeSessionId = getKey(sessionId);
		String serializeValue = ObjectTransformer.serialize(session);
    	cacheService.setCacheObject(serializeSessionId,serializeValue);
		cacheService.expire(serializeSessionId, authJwkConfig.getRefreshExpires());
	}

	@Override
	public Session remove(String sessionId) {
		String serializeSessionId = getKey(sessionId);
		String value = cacheService.getCacheObject(serializeSessionId);
		if (StringUtils.isNotEmpty(value)) {
			Session session = ObjectTransformer.deserialize(value);
			cacheService.deleteObject(sessionId);
			return session;
		}
		return null;
	}

    @Override
    public Session get(String sessionId) {
		String serializeSessionId = getKey(sessionId);
		String value = cacheService.getCacheObject(serializeSessionId);
		if (StringUtils.isNotEmpty(value)) {
			Session session = ObjectTransformer.deserialize(value);
			return session;
		}
		return null;
    }

    @Override
    public Session refresh(String sessionId,LocalDateTime refreshTime) {
		String serializeSessionId = getKey(sessionId);
		String value = cacheService.getCacheObject(serializeSessionId);
		if (StringUtils.isNotEmpty(value)) {
			Session session = ObjectTransformer.deserialize(value);
        	log.debug("refresh session Id {} at refreshTime {}",sessionId,refreshTime);
	        session.setLastAccessTime(refreshTime);
	        //renew one
	        create(sessionId,session);

			return session;
        }
		return null;
    }

    @Override
    public Session refresh(String sessionId) {
		String serializeSessionId = getKey(sessionId);
		String value = cacheService.getCacheObject(serializeSessionId);
		if (StringUtils.isNotEmpty(value)) {
			Session session = ObjectTransformer.deserialize(value);
        	LocalDateTime currentTime = LocalDateTime.now();
			log.debug("refresh session Id {} at time {}",sessionId,currentTime);
        	session.setLastAccessTime(currentTime);
        	//put renew session
	        create(sessionId , session);

			return session;
        }
        return null;
    }

	public String getKey(String sessionId) {
		return String.format(SESSION_PRX_KEY,sessionId);
	}

}
