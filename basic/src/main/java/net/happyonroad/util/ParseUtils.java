/**
 * Developer: Kadvin Date: 14/12/30 上午10:28
 */
package net.happyonroad.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * <h1>简单的工具</h1>
 */
public final class ParseUtils {
    public static ObjectMapper mapper = new ObjectMapper();

    static {
        JacksonJmxModule module = new JacksonJmxModule();
        mapper.registerModule(module);
    }

    private ParseUtils() {

    }

    public static int parseInt(Object string, int defaultValue) {
        if (string == null) return defaultValue;
        return Integer.valueOf(string.toString());
    }

    public static String parseString(Object string, String defaultValue) {
        if (string == null) return defaultValue;
        return string.toString();
    }

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

    public static String toJSONString(Object any) {
        try {
            return mapper.writeValueAsString(any);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while convert: " + any + " as json", e);
        }
    }
}
