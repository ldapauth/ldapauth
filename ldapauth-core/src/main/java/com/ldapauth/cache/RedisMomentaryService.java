package com.ldapauth.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RedisMomentaryService implements MomentaryService {


    private static final Logger _logger = LoggerFactory.getLogger(RedisMomentaryService.class);

	protected int validitySeconds 	= 60 * 5; //default 5 minutes.

	public static String PREFIX 	= "MXK_MOMENTARY_";

	/**
	 *
	 */
	public RedisMomentaryService() {

	}


	@Override
	public  void put(String sessionId , String name, Object value){
		put(getSessionKey(sessionId , name), value);
	}

    @Override
    public Object get(String sessionId , String name) {
    	return get(getSessionKey(sessionId , name));
    }

	@Override
	public Object remove(String sessionId, String name) {
		return remove(getSessionKey(sessionId , name));
	}

    private String getSessionKey(String sessionId , String name) {
    	return PREFIX + sessionId + "_" + name;
    }

	@Override
	public void put(String key, Object value) {

	}

	@Override
	public Object get(String key) {

        return null;
	}

	@Override
	public Object remove(String key) {
        return null;
	}

}
