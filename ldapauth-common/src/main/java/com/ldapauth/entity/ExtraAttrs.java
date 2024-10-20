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


package com.ldapauth.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.ldapauth.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtraAttrs {
	final static Logger _logger = LoggerFactory.getLogger(ExtraAttrs.class);

	ArrayList <ExtraAttr> extraAttrs ;

	/**
	 *
	 */
	public ExtraAttrs() {
		super();
	}

	/**
	 *
	 */
	public ExtraAttrs(String arrayJsonString) {
		String extraAttrsJsonString = "{\"extraAttrs\":" + arrayJsonString + "}";
		_logger.debug("Extra Attrs Json String {}" , extraAttrsJsonString);
		ExtraAttrs extraAttrs = JsonUtils.gsonStringToObject(extraAttrsJsonString, ExtraAttrs.class);
		this.extraAttrs = extraAttrs.getExtraAttrs();
	}



	public void put(String attr,String value) {
		if(extraAttrs == null){
			extraAttrs = new ArrayList<ExtraAttr>();
		}
		this.extraAttrs.add(new ExtraAttr(attr,value));
	}

	public void put(String attr,String type,String value) {
		if(extraAttrs == null){
			extraAttrs = new ArrayList<ExtraAttr>();
		}
		this.extraAttrs.add(new ExtraAttr(attr,type,value));
	}

	public String get(String attr) {
		String value = null;
		if(extraAttrs != null && extraAttrs.size() != 0){
			for(ExtraAttr extraAttr :extraAttrs){
				if(extraAttr.getAttr().equals(attr)){
					value = extraAttr.getValue();
				}
			}
		}
		return value;
	}

	public String toJsonString(){
		String jsonString =JsonUtils.gsonToString(extraAttrs);
		_logger.debug("jsonString {}" , jsonString);
		return jsonString;
	}

	public HashMap<String,ExtraAttr > toHashMap(){
		HashMap<String,ExtraAttr > extraAttrsHashMap = new HashMap<String,ExtraAttr >();
		for(ExtraAttr extraAttr : extraAttrs){
			extraAttrsHashMap.put(extraAttr.getAttr(), extraAttr);
		}
		_logger.debug("extraAttrs HashMap {}" , extraAttrsHashMap);
		return extraAttrsHashMap;
	}

	public Properties toProperties(){
		Properties properties=new Properties();
		for(ExtraAttr extraAttr :extraAttrs){
			properties.put(extraAttr.getAttr(), extraAttr.getValue());
		}
		_logger.debug("extraAttrs Properties {}" ,properties);
		return properties;
	}

	public ArrayList<ExtraAttr> getExtraAttrs() {
		return extraAttrs;
	}

	public void setExtraAttrs(ArrayList<ExtraAttr> extraAttrs) {
		this.extraAttrs = extraAttrs;
	}

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExtraAttrs [extraAttrs=");
        builder.append(extraAttrs);
        builder.append("]");
        return builder.toString();
    }


}
