package net.happyonroad.mongo.convert;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

import java.util.Properties;

/**
 * <h1>Properties 2 DBObject</h1>
 *
 * @author Jay Xiong
 */
public class Properties2DBObject implements Converter<Properties, DBObject> {
    @Override
    public DBObject convert(Properties source) {
        if (source == null) return null;
        BasicDBObject object = new BasicDBObject();
        for (String key : source.stringPropertyNames()) {
            String value = source.getProperty(key);
            String newKey = encodeKey(key);
            object.put(newKey, value);
        }
        return object;
    }

    public static String encodeKey(String key) {
        //replace \ -> \\, . -> u002e, $ -> u0024
        return key.replaceAll("\\\\", "\\\\\\\\")
                  .replaceAll("\\.", "\\\\u002e")
                  .replaceAll("\\$", "\\\\u0024");
    }
}
