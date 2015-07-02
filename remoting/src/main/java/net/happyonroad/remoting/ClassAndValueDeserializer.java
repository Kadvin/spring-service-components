/**
 * Developer: Kadvin Date: 15/2/25 上午11:26
 */
package net.happyonroad.remoting;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import net.happyonroad.extension.GlobalClassLoader;

import java.io.IOException;

/**
 * Deserialize ClassAndValue
 */
public class ClassAndValueDeserializer extends JsonDeserializer<ClassAndValue> {
    @Override
    public ClassAndValue deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        if( jp.getCurrentToken() == JsonToken.START_OBJECT){
            ClassAndValue pair = new ClassAndValue();
            while(jp.getCurrentToken() != JsonToken.END_OBJECT){
                jp.nextToken();
                if( jp.getCurrentToken() == JsonToken.FIELD_NAME){
                    String fieldName = jp.getText();
                    jp.nextToken();
                    if( "klass".equals(fieldName) ){
                        try {
                            pair.klass = Class.forName(jp.getText(), true, GlobalClassLoader.getInstance());
                        } catch (ClassNotFoundException e) {
                            throw new JsonMappingException("Can't convert class " + jp.getText(), e);
                        }
                    }else if("value".equals(fieldName) ){
                        pair.value = jp.readValueAs(pair.klass);
                    }
                }
            }
            return pair;
        }else{
            throw new JsonMappingException("It should starts with object flag: {");
        }
    }
}
