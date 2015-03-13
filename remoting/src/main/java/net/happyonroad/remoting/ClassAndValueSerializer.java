/**
 * Developer: Kadvin Date: 15/2/25 上午11:21
 */
package net.happyonroad.remoting;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Serialize class and value
 */
public class ClassAndValueSerializer extends JsonSerializer<ClassAndValue> {
    @Override
    public void serialize(ClassAndValue pair, JsonGenerator jgen, SerializerProvider provider)
            throws IOException{
        jgen.writeStartObject();
        jgen.writeStringField("klass", pair.klass.getName());
        jgen.writeObjectField("value", pair.value);
        jgen.writeEndObject();
    }
}
