package net.happyonroad.model;

import java.util.Map;

/**
 * <h1>一个通用的Map，可以方便的进行数据类型的转换</h1>
 * 其key为固定的字符串类型，存取大小写不敏感
 *
 * @author Jay Xiong
 */
public interface GeneralMap<V> extends Map<String, V> {

    String getString(String key);

    Boolean getBoolean(String key);

    Integer getInteger(String key);

    Short getShort(String key);

    Byte getByte(String key);

    Long getLong(String key);

    Float getFloat(String key);

    Double getDouble(String key);
}
