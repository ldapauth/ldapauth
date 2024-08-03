package com.ldapauth.cache;

import cn.hutool.extra.spring.SpringUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 缓存基本操作方法服务
 */
@Slf4j
@Service
public class CacheService {

	@Value("${spring.redis.enabled:false}")
	boolean redisEnabled;

	private RedisTemplate getRedisTemplate(){
		return SpringUtil.getBean("redisTemplate",RedisTemplate.class);
	}

	static final int validitySeconds 	=2 * 60 * 60; //default 2 hours.


	static final long CACHE_MAXIMUM_SIZE = 100000000;

	private final static Cache<String, Object> momentaryStore =
			Caffeine.newBuilder()
					.expireAfterWrite(validitySeconds,TimeUnit.SECONDS)
					.maximumSize(CACHE_MAXIMUM_SIZE)
					.build();

	/**
	 * 设置缓存基本的对象
	 *
	 * @param key       缓存的键值
	 * @param value     缓存的值
	 */
	public <T> void setCacheObject(final String key, final T value) {
		if (redisEnabled) {
			getRedisTemplate().opsForValue().set(key,value);
		} else {
			momentaryStore.put(key,value);
		}
	}

	/**
	 * 缓存基本的对象，Integer、String、实体类等
	 *
	 * @param key      缓存的键值
	 * @param value    缓存的值
	 * @param time    时间
	 * @param timeUnit 单位
	 */
	public <T> void setCacheObject(final String key, final T value,long time,TimeUnit timeUnit) {
		if (redisEnabled) {
			getRedisTemplate().opsForValue().set(key,value);
			getRedisTemplate().expire(key,time,timeUnit);
		} else {
			momentaryStore.put(key,value);
		}
	}


	/**
	 * 获得缓存的基本对象。
	 *
	 * @param key 缓存键值
	 * @return 缓存键值对应的数据
	 */
	public  <T> T getCacheObject(final String key) {
		if (redisEnabled) {
			return (T) getRedisTemplate().opsForValue().get(key);
		} else {
			return (T) momentaryStore.getIfPresent(key);
		}
	}

	/**
	 * 删除单个对象
	 *
	 * @param key 缓存的键值
	 */
	public boolean deleteObject(final String key) {
		if (redisEnabled) {
			return getRedisTemplate().delete(key);
		} else {
			momentaryStore.invalidate(key);
			return true;
		}
	}


	/**
	 * 设置有效时间
	 *
	 * @param key     Redis键
	 * @param timeout 超时时间 单位秒

	 */
	public  boolean expire(final String key, final long timeout) {
		return expire(key, timeout,TimeUnit.SECONDS);
	}

	/**
	 * 设置有效时间
	 *
	 * @param key     Redis键
	 * @param timeout 超时时间 单位秒
	 * @param timeUnit 单位

	 */
	public  boolean expire(final String key, final long timeout,TimeUnit timeUnit) {
		if (redisEnabled) {
			return getRedisTemplate().expire(key,timeout,timeUnit);
		} else {
			Object o = momentaryStore.getIfPresent(key);
			if(Objects.nonNull(o)) {
				momentaryStore.invalidate(key);
				momentaryStore.put(key,o);
			}
			return true;
		}
	}
}
