package com.ldapauth.authn.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * congress Redis缓存服务
 *
 * @author Crystal.Sea
 *
 */
public class RedisCongressService implements CongressService {
    private static final Logger _logger = LoggerFactory.getLogger(RedisCongressService.class);

    /**
     * 默认有效时间3分钟
     */
	protected int validitySeconds = 60 * 3; //default 3 minutes.


	public static String PREFIX="MXK_CONGRESS_";
	/**
	 * @param connectionFactory
	 */
	public RedisCongressService() {
		super();
	}


	/**
	 * 存储
	 */
	@Override
	public void store(String congress, AuthJwt authJwt) {

	}

	/**
	 * 删除
	 */
	@Override
	public AuthJwt remove(String congress) {

		return null;
	}

	/**
	 * 读取
	 */
    @Override
    public AuthJwt get(String congress) {
        return null;
    }

    /**
     * 读取
     */
	@Override
	public AuthJwt consume(String congress) {
		return null;
	}


}
