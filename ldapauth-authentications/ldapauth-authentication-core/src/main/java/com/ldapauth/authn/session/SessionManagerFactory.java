package com.ldapauth.authn.session;

import java.time.LocalDateTime;

import com.ldapauth.cache.CacheService;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.configuration.AuthJwkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SessionManager/会话管理工厂
 * Level 1 in memory,store in Caffeine/一级缓存，存储内存中，采用Caffeine实现
 * Level 2 in Redis /二级缓存存储Redis中
 * user session status in database/用户会话状态存储在数据库中
 * sessionstatus 1有效 7无效
 *
 * @author Crystal.Sea
 *
 */
public class SessionManagerFactory implements SessionManager{
	private static final  Logger _logger = LoggerFactory.getLogger(SessionManagerFactory.class);

	private CacheySessionManager cacheySessionManager;


	public SessionManagerFactory(CacheService cacheService, AuthJwkConfig authJwkConfig) {
		this.cacheySessionManager = new CacheySessionManager(cacheService,authJwkConfig);
		_logger.debug("CacheySessionManager");
	}

	/**
	 * 创建
	 */
	public void create(String sessionId, Session session) {
		cacheySessionManager.create(sessionId, session);
	}

	/**
	 * 删除
	 */
	public Session remove(String sessionId) {
		return  cacheySessionManager.remove(sessionId);
	}

	/**
	 * 读取，先读取本地缓存再读取Redis
	 */
	public Session get(String sessionId) {
		return cacheySessionManager.get(sessionId);
	}

	/**
	 * 根据会话id和刷新时间刷新，先刷新Redis，再刷新本地缓存
	 */
	public Session refresh(String sessionId, LocalDateTime refreshTime) {
		return cacheySessionManager.refresh(sessionId,refreshTime);
	}

	/**
	 * 根据会话id刷新，先刷新Redis，再刷新本地缓存
	 */
	public Session refresh(String sessionId) {
		return cacheySessionManager.refresh(sessionId);
	}

}
