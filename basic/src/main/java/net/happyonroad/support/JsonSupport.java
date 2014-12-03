/**
 * @author XiongJie, Date: 13-11-20
 */
package net.happyonroad.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.happyonroad.model.Jsonable;

import java.io.IOException;
import java.io.InputStream;

/** Description */
public class JsonSupport extends BinarySupport implements Jsonable {
    private static final long serialVersionUID = -5873792929187680622L;
    protected static ObjectMapper mapper = new ObjectMapper();
    /**
     * Parse Json String to Target Object
     *
     * @param content  json string
     * @param theClass target object class
     * @param <T>      target type
     * @return target instance
     */
    public static <T> T parseJson(String content, Class<T> theClass) {
        T t;
        try {
            t = mapper.readValue(content, theClass);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't parse " + content + " to " + theClass.getSimpleName(), e);
        }
        if (t == null) {
            throw new IllegalArgumentException("Can't parse " + content + " to " + theClass.getSimpleName());
        }
        return t;
    }

    public static <T> T  parseJson(InputStream stream, Class<T> clazz) {
        T t;
        try {
            t = mapper.readValue(stream, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't parse stream to " + clazz.getSimpleName(), e);
        }
        if (t == null) {
            throw new IllegalArgumentException("Can't parse stream to " + clazz.getSimpleName());
        }
        return t;
    }

    public String toJson() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while convert " + getClass().getSimpleName() + " as json", e);
        }
    }

    @Override
    public String toString() {
        return toJson();
    }


    protected int parseInt(Object string, int defaultValue) {
        if( string == null ) return defaultValue;
        return Integer.valueOf(string.toString());
    }

    public static String toJSONString(Object any) {
        try {
            return mapper.writeValueAsString(any);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while convert: " + any + " as json", e);
        }
    }
}
