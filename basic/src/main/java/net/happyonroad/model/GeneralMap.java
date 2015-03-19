package net.happyonroad.model;

import java.util.Map;

/**
 * <h1>一个通用的Map，可以方便的进行数据类型的转换</h1>
 *
 * @author Jay Xiong
 */
public interface GeneralMap<K,V> extends Map<K, V> {
    String getString(K key);
    Boolean getBoolean(K key);
    Integer getInteger(K key);
    Short getShort(K key);
    Byte getByte(K key);
    Long getLong(K key);
    Float getFloat(K key);
    Double getDouble(K key);
}
