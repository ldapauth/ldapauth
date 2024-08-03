package com.ldapauth.authz.oauth2.common.util;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * @author Dave Syer
 *
 */
public class Jackson2JsonParser implements JsonParser {

	private ObjectMapper mapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> parseMap(String json) {
		try {
			return mapper.readValue(json, Map.class);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Cannot parse json", e);
		}
	}

	@Override
	public String formatMap(Map<String, ?> map) {
		try {
			return mapper.writeValueAsString(map);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Cannot format json", e);
		}
	}

}
