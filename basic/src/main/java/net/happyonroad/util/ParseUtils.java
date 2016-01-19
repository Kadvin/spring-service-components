/**
 * Developer: Kadvin Date: 14/12/30 上午10:28
 */
package net.happyonroad.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * <h1>简单的工具</h1>
 */
public final class ParseUtils {
    public static ThreadLocal<ExtendedMapper> mapper = new ThreadLocal<ExtendedMapper>();
    static JacksonJmxModule module = new JacksonJmxModule();

    static {
        refreshMapper();
    }


    private ParseUtils() {
    }

    public static void refreshMapper() {
        //Renew all threads mapper
        mapper = new ThreadLocal<ExtendedMapper>();
    }

    public static ExtendedMapper getMapper(){
        if( mapper.get() == null ){
            mapper.set(new ExtendedMapper());
            mapper.get().registerModule(module);
        }
        return mapper.get();
    }

    public static int parseInt(Object string, int defaultValue) {
        if (string == null) return defaultValue;
        return Integer.valueOf(string.toString());
    }

    public static String parseString(Object string, String defaultValue) {
        if (string == null) return defaultValue;
        return string.toString();
    }

    public static boolean parseBoolean(Object enabled, boolean defaultValue) {
        if (enabled == null) return defaultValue;
        return Boolean.valueOf(enabled.toString());
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
        return parseJson(content, theClass, null);
    }

    public static <T> T parseJson(String content, Class<T> theClass, Class viewClass) {
        return parse(content, theClass, viewClass);
    }

    public static <T> T parseJson(String content, JavaType javaType) {
        return parseJson(content, javaType, null);
    }

    public static <T> T parseJson(String content, JavaType javaType, Class viewClass) {
        return parse(content, javaType, viewClass);
    }

    public static <T> T parseJson(String content, TypeReference typeReference) {
        return parseJson(content, typeReference, null);
    }

    public static <T> T parseJson(String content, TypeReference typeReference, Class viewClass) {
        return parse(content, typeReference, viewClass);
    }

    @SuppressWarnings("unchecked")
    static <T> T parse(String content, Object type, Class viewClass) {
        T t;
        String name;
        if (type instanceof TypeReference) {
            name = type.toString();
        } else if (type instanceof JavaType) {
            name = type.toString();
        } else {
            //class
            name = ((Class) type).getSimpleName();
        }
        try {
            if (viewClass != null) {
                DeserializationConfig oldConfig = getMapper().getDeserializationConfig();
                DeserializationConfig newConfig = oldConfig.withView(viewClass);
                try {
                    getMapper().setDeserializationConfig(newConfig);
                    ObjectReader reader;
                    if (type instanceof TypeReference) {
                        reader = getMapper().readerWithView(viewClass).withType((TypeReference<T>) type);
                    } else if (type instanceof JavaType) {
                        reader = getMapper().readerWithView(viewClass).withType((JavaType) type);
                    } else {
                        reader = getMapper().readerWithView(viewClass).withType((Class<T>) type);
                    }
                    return reader.readValue(content);
                } finally {
                    getMapper().setDeserializationConfig(oldConfig);
                }
            } else {
                if (type instanceof TypeReference) {
                    t = getMapper().readValue(content, (TypeReference<T>) type);
                } else if (type instanceof JavaType) {
                    t = getMapper().readValue(content, (JavaType) type);
                } else {
                    t = getMapper().readValue(content, (Class<T>) type);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't parse " + content + " to " + name, e);
        }
        if (t == null) {
            throw new IllegalArgumentException("Can't parse " + content + " to " + name);
        }
        return t;
    }

    public static <T> T parseJson(InputStream stream, Class<T> clazz) {
        T t;
        try {
            t = getMapper().readValue(stream, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't parse stream to " + clazz.getSimpleName(), e);
        }
        if (t == null) {
            throw new IllegalArgumentException("Can't parse stream to " + clazz.getSimpleName());
        }
        return t;
    }

    public static String toJSONString(Object any) {
        return toJSONString(any, null);
    }

    public static String toJSONString(Object any, Class viewClass) {
        try {
            if (viewClass != null) {
                SerializationConfig oldConfig = getMapper().getSerializationConfig();
                SerializationConfig newConfig = oldConfig.withView(viewClass);
                try {
                    getMapper().setSerializationConfig(newConfig);
                    ObjectWriter writer = getMapper().writerWithView(viewClass);
                    return writer.writeValueAsString(any);
                } finally {
                    getMapper().setSerializationConfig(oldConfig);
                }
            } else {
                return getMapper().writeValueAsString(any);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while convert: " + any + " as json", e);
        }
    }

}
