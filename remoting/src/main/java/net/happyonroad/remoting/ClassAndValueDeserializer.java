/**
 * Developer: Kadvin Date: 15/2/25 上午11:26
 */
package net.happyonroad.remoting;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Deserialize ClassAndValue
 */
public class ClassAndValueDeserializer extends JsonDeserializer<InvocationRequestMessage.ClassAndValue[]> {
    @Override
    public InvocationRequestMessage.ClassAndValue[] deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        List<InvocationRequestMessage.ClassAndValue> pairs = new LinkedList<InvocationRequestMessage.ClassAndValue>();
        if( jp.isExpectedStartArrayToken() ){
            while(jp.getCurrentToken() != JsonToken.END_ARRAY){
                jp.nextToken();
                if( jp.getCurrentToken() == JsonToken.START_OBJECT){
                    InvocationRequestMessage.ClassAndValue pair = new InvocationRequestMessage.ClassAndValue();
                    while(jp.getCurrentToken() != JsonToken.END_OBJECT){
                        jp.nextToken();
                        if( jp.getCurrentToken() == JsonToken.FIELD_NAME){
                            String fieldName = jp.getText();
                            jp.nextToken();
                            if( "klass".equals(fieldName) ){
                                try {
                                    pair.klass = Class.forName(jp.getText());
                                } catch (ClassNotFoundException e) {
                                    throw new JsonMappingException("Can't convert class " + jp.getText(), e);
                                }
                            }else if("value".equals(fieldName) ){
                                pair.value = jp.readValueAs(pair.klass);
                            }
                        }
                    }
                    pairs.add(pair);
                }
            }
        }
        return pairs.toArray(new InvocationRequestMessage.ClassAndValue[pairs.size()]);
    }
}
