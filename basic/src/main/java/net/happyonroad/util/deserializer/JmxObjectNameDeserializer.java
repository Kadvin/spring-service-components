package net.happyonroad.util.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.IOException;

/**
 * <h1>javax.management.ObjectName Deserializer</h1>
 *
 * @author Jay Xiong
 */
public class JmxObjectNameDeserializer extends StdScalarDeserializer<ObjectName> {

    private static final long serialVersionUID = -8831469525395023908L;

    public JmxObjectNameDeserializer() {
        super(ObjectName.class);
    }

    @Override

    public ObjectName deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        if( jp.getCurrentToken() == JsonToken.START_OBJECT){
            String name = null;
            while(jp.getCurrentToken() != JsonToken.END_OBJECT){
                jp.nextToken();
                if( jp.getCurrentToken() == JsonToken.FIELD_NAME){
                    String fieldName = jp.getText();
                    jp.nextToken();
                    if( "canonicalName".equals(fieldName) ){
                        name = jp.getText();
                    }
                }
            }
            if(name == null )
                throw new JsonMappingException("ObjectName canonicalName is mandatory");
            try {
                return new ObjectName(name);
            } catch (MalformedObjectNameException e) {
                throw new JsonMappingException(name+ " is an invalid CanonicalName", e);
            }
        }else{
            throw new JsonMappingException("It should starts with object flag: {");
        }
    }
}
