package net.happyonroad.mongo.convert;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

import java.util.Properties;

/**
 * <h1>DBObject 2 Properties</h1>
 *
 * @author Jay Xiong
 */
public class DBObject2Properties implements Converter<DBObject, Properties> {
    @Override
    public Properties convert(DBObject source) {
        if( source == null ) return null;
        Properties properties = new Properties();
        for(String key : source.keySet()){
            String value = (String)source.get(key);
            String convertedKey = decodeKey(key);
            properties.setProperty(convertedKey, value);
        }
        return properties;
    }

    public static String decodeKey(String key) {
        //replace \ <- \\, . <- u002e, $ <- u0024
        return key.replaceAll("\\\\\\\\", "\\\\")
                  .replaceAll("\\\\u002e", "\\.")
                  .replaceAll("\\\\u0024", "\\$");
    }
}
