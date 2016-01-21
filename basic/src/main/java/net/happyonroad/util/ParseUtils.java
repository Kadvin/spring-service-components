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

    private ParseUtils() {
    }

    public static ExtendedMapper createMapper(){
        ExtendedMapper mapper = new ExtendedMapper();
        mapper.registerModule(new JacksonJmxModule());
        return mapper;
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
            ExtendedMapper mapper = createMapper();
            if (viewClass != null) {
                DeserializationConfig oldConfig = mapper.getDeserializationConfig();
                DeserializationConfig newConfig = oldConfig.withView(viewClass);
                try {
                    mapper.setDeserializationConfig(newConfig);
                    ObjectReader reader;
                    if (type instanceof TypeReference) {
                        reader = mapper.readerWithView(viewClass).withType((TypeReference<T>) type);
                    } else if (type instanceof JavaType) {
                        reader = mapper.readerWithView(viewClass).withType((JavaType) type);
                    } else {
                        reader = mapper.readerWithView(viewClass).withType((Class<T>) type);
                    }
                    return reader.readValue(content);
                } finally {
                    mapper.setDeserializationConfig(oldConfig);
                }
            } else {
                if (type instanceof TypeReference) {
                    t = mapper.readValue(content, (TypeReference<T>) type);
                } else if (type instanceof JavaType) {
                    t = mapper.readValue(content, (JavaType) type);
                } else {
                    t = mapper.readValue(content, (Class<T>) type);
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
        ExtendedMapper mapper = createMapper();
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
        return toJSONString(any, null);
    }

    public static String toJSONString(Object any, Class viewClass) {
        try {
            ExtendedMapper mapper = createMapper();
            if (viewClass != null) {
                SerializationConfig oldConfig = mapper.getSerializationConfig();
                SerializationConfig newConfig = oldConfig.withView(viewClass);
                try {
                    mapper.setSerializationConfig(newConfig);
                    ObjectWriter writer = mapper.writerWithView(viewClass);
                    return writer.writeValueAsString(any);
                } finally {
                    mapper.setSerializationConfig(oldConfig);
                }
            } else {
                return mapper.writeValueAsString(any);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while convert: " + any + " as json", e);
        }
    }

}
