package net.happyonroad.util.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import javax.management.Attribute;
import java.io.IOException;

/**
 * <h1>javax.management.Attribute Deserializer</h1>
 *
 * @author Jay Xiong
 */
public class JmxAttributeDeserializer extends StdScalarDeserializer<Attribute> {

    private static final long serialVersionUID = -8831469525395023908L;

    public JmxAttributeDeserializer() {
        super(Attribute.class);
    }

    @Override

    public Attribute deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        if( jp.getCurrentToken() == JsonToken.START_OBJECT){
            String name = null, value = null;
            while(jp.getCurrentToken() != JsonToken.END_OBJECT){
                jp.nextToken();
                if( jp.getCurrentToken() == JsonToken.FIELD_NAME){
                    String fieldName = jp.getText();
                    jp.nextToken();
                    if( "name".equals(fieldName) ){
                        name = jp.getText();
                    }else if("value".equals(fieldName) ){
                        value = jp.getText();
                    }
                }
            }
            if(name == null )
                throw new JsonMappingException("Attribute name is mandatory");
            return new Attribute(name, value);
        }else{
            throw new JsonMappingException("It should starts with object flag: {");
        }
    }
}
