package net.happyonroad.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import javax.management.ObjectName;
import java.io.IOException;

/**
 * <h1>javax.management.ObjectName Serializer</h1>
 *
 * @author Jay Xiong
 */
public class JmxObjectNameSerializer extends StdScalarSerializer<ObjectName> {
    public JmxObjectNameSerializer() {
        super(ObjectName.class);
    }

    @Override
    public void serialize(ObjectName value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();
        jgen.writeObjectField("canonicalName", value.getCanonicalName());
        jgen.writeEndObject();
    }
}
