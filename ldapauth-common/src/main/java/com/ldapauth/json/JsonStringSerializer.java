


package com.ldapauth.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * Integer json序列化格式.
 *
 * @author Crystal.Sea
 *
 */

@Component
public class JsonStringSerializer extends JsonSerializer<Integer> {

    @Override
    public void serialize(Integer value, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        gen.writeString(value.toString());
    }
}
