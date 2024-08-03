package com.ldapauth.authz.oauth2.common.util;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * JSON deserializer for Jackson to handle regular date instances as timestamps in ISO format.
 *
 * @author Dave Syer
 *
 */
public class JsonDateDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


	@Override
	public Date deserialize(com.fasterxml.jackson.core.JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		try {
			synchronized (dateFormat) {
				return dateFormat.parse(parser.toString());
			}
		}
		catch (ParseException e) {
			throw new JsonParseException( parser,"Could not parse date ",e);
		}
	}

}
