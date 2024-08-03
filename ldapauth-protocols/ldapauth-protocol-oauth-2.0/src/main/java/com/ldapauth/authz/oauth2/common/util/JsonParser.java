package com.ldapauth.authz.oauth2.common.util;

import java.util.Map;

/**
 * @author Dave Syer
 *
 */
public interface JsonParser {

	/**
	 * Parse the specified JSON string into a Map.
	 * @param json the JSON to parse
	 * @return the parsed JSON as a map
	 */
	Map<String, Object> parseMap(String json);

	/**
	 * Convert the Map to JSON
	 * @param map a map to format
	 * @return a JSON representation of the map
	 */
	String formatMap(Map<String, ?> map);

}
