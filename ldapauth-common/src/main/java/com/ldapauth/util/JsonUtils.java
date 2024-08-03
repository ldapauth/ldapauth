


package com.ldapauth.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;

public class JsonUtils {

    /**
     * jackson Transform json string to java bean object.
     *
     * @param json String
     * @param bean Object
     * @return Object
     */
    public static Object stringToObject(String json, Object bean) {
        try {
            bean = (new ObjectMapper()).readValue(json, bean.getClass());
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * jackson Transform json string to java bean object.
     *
     * @param json String
     * @param cls Class
     * @return Object
     */
    public static <T> T stringToObject(String json, Class<T> cls) {
        T bean = null;
        try {
            bean = (new ObjectMapper()).readValue(json, cls);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * jackson Transform java bean object to json string.
     *
     * @param bean Object
     * @return string
     */
    public static String toString(Object bean) {
        String json = "";
        try {
            json = (new ObjectMapper()).writeValueAsString(bean);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Gson Transform json string to java bean object .
     *
     * @param <T> Class
     * @param json String
     * @return Object
     */

    public static <T> T gsonStringToObject(String json, Class<T> cls) {
        T newBean = (new Gson()).fromJson(json, cls);
        return newBean;
    }

    /**
     * Gson Transform java bean object to json string.
     *
     * @param bean Object
     * @return string
     */
    public static String gsonToString(Object bean) {
        String json = "";
        // convert java object to JSON format,
        // and returned as JSON formatted string
        json = (new Gson()).toJson(bean);

        return json;
    }

}
