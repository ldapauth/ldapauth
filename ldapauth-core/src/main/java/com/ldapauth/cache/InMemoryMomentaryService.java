/*
 * Copyright [2020] [ldapauth of copyright http://www.ldapauth.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ldapauth.cache;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;


public class InMemoryMomentaryService implements MomentaryService{
    private static final Logger _logger = LoggerFactory.getLogger(InMemoryMomentaryService.class);

    static final 	long 	CACHE_MAXIMUM_SIZE 	= 2000000;

	protected  static  Cache<String, Object> momentaryStore =
        	        Caffeine.newBuilder()
        	            .expireAfterWrite(5, TimeUnit.MINUTES)
        	            .maximumSize(CACHE_MAXIMUM_SIZE)
        	            .build();

	public InMemoryMomentaryService() {
        super();
    }

    @Override
    public  void put(String sessionId , String name, Object value){
    	put(getSessionKey(sessionId,name), value);
	}

	@Override
	public Object remove(String sessionId , String name) {
		return remove(getSessionKey(sessionId,name));
	}

    @Override
    public Object get(String sessionId , String name) {
    	return get(getSessionKey(sessionId,name));
    }


    private String getSessionKey(String sessionId , String name) {
    	return sessionId + "_" + name;
    }

	@Override
	public void put(String key, Object value) {
		momentaryStore.put(key, value);
	}

	@Override
	public Object get(String key) {
		 _logger.trace("key {}" , key);
		return momentaryStore.getIfPresent(key);
	}

	@Override
	public Object remove(String key) {
		Object value = momentaryStore.getIfPresent(key);
		momentaryStore.invalidate(key);
		_logger.trace("key {}, value {}",key , value);
		return value;
	}
}
