package com.ldapauth.cache;

public interface MomentaryService {

    void put(String key , String name, Object value);

    Object get(String key , String name);

    Object remove(String key , String name);

    //use key
    void put(String key, Object value);

    Object get(String key);

    Object remove(String key);

}
