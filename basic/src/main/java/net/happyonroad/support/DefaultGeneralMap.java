package net.happyonroad.support;

import net.happyonroad.model.GeneralMap;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>The default general map</h1>
 * 这个map存储的字段是原有的key/value，但是，获取/remove字段时，大小写不敏感
 *
 * @author Jay Xiong
 */
public class DefaultGeneralMap<V> extends HashMap<String, V> implements GeneralMap<V> {
    private static final long serialVersionUID = -8898499680831724783L;
    //记录了小写的key到原始的key之间的映射
    //通过这个机制，既保证了大小写不敏感，又没有破坏原有的map信息（key没有变化）
    Map<String, String> keyMapping = new HashMap<String, String>();

    String originKey(String key) {
        return keyMapping.get(key.toLowerCase());
    }

    @Override
    public V get(Object key) {
        String originKey = originKey((String) key);
        return super.get(originKey);
    }

    @Override
    public V remove(Object key) {
        String originKey = originKey((String) key);
        return super.remove(originKey);
    }

    @Override
    public V put(String key, V value) {
        // 防止存在多个不同的大小写的key，如: abc -> 1, Abc -> 2
        remove(key);
        keyMapping.put(key.toLowerCase(), key);
        return super.put(key, value);
    }

    /**
     * <h2>更改key的名称</h2>
     *
     * @param oldKey 原有的key
     * @param newKey 新的key
     */
    public void rename(String oldKey, String newKey) {
        V value = remove(oldKey);
        put(newKey, value);
    }

    @Override
    public String getString(String key) {
        V v = get(key);
        return v == null ? null : v.toString();
    }

    @Override
    public Boolean getBoolean(String key) {
        V v = get(key);
        if (v instanceof Number) return ((Number) v).intValue() != 0;
        return v == null ? null : Boolean.getBoolean(v.toString().trim());
    }

    @Override
    public Integer getInteger(String key) {
        V v = get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        return v == null ? 0 : Integer.valueOf(v.toString().trim());
    }

    @Override
    public Short getShort(String key) {
        V v = get(key);
        if (v instanceof Number) return ((Number) v).shortValue();
        return v == null ? 0 : Short.valueOf(v.toString().trim());
    }

    @Override
    public Byte getByte(String key) {
        V v = get(key);
        if (v instanceof Number) return ((Number) v).byteValue();
        return v == null ? 0 : Byte.valueOf(v.toString().trim());
    }

    @Override
    public Long getLong(String key) {
        V v = get(key);
        if (v instanceof Number) return ((Number) v).longValue();
        return v == null ? 0 : Long.valueOf(v.toString());
    }

    @Override
    public Float getFloat(String key) {
        V v = get(key);
        if (v instanceof Number) return ((Number) v).floatValue();
        return v == null ? 0 : Float.valueOf(v.toString().trim());
    }

    @Override
    public Double getDouble(String key) {
        V v = get(key);
        if (v instanceof Number) return ((Number) v).doubleValue();
        return v == null ? 0 : Double.valueOf(v.toString().trim());
    }

    public static <Y> DefaultGeneralMap<Y> parse(Map<String, Y> map) {
        DefaultGeneralMap<Y> result = new DefaultGeneralMap<Y>();
        for (String key : map.keySet()) {
            result.put(key, map.get(key));
        }
        return result;
    }


}
