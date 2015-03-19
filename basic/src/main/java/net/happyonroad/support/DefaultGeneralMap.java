package net.happyonroad.support;

import net.happyonroad.model.GeneralMap;

import java.util.HashMap;

/**
 * <h1>The default general map</h1>
 *
 * @author Jay Xiong
 */
public class DefaultGeneralMap<K,V> extends HashMap<K,V> implements GeneralMap<K,V>{
    private static final long serialVersionUID = -8898499680831724783L;

    @Override
    public String getString(K key) {
        V v = get(key);
        return v == null ? null : v.toString();
    }

    @Override
    public Boolean getBoolean(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).intValue() != 0 ;
        return v == null ? null : Boolean.getBoolean(v.toString());
    }

    @Override
    public Integer getInteger(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).intValue();
        return v == null ? 0 : Integer.valueOf(v.toString());
    }

    @Override
    public Short getShort(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).shortValue();
        return v == null ? 0 : Short.valueOf(v.toString());
    }

    @Override
    public Byte getByte(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).byteValue();
        return v == null ? 0 : Byte.valueOf(v.toString());
    }

    @Override
    public Long getLong(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).longValue();
        return v == null ? 0 : Long.valueOf(v.toString());
    }

    @Override
    public Float getFloat(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).floatValue();
        return v == null ? 0 : Float.valueOf(v.toString());
    }

    @Override
    public Double getDouble(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).doubleValue();
        return v == null ? 0 : Double.valueOf(v.toString());
    }
}
