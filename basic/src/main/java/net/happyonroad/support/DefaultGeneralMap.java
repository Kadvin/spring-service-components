package net.happyonroad.support;

import net.happyonroad.model.GeneralMap;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>The default general map</h1>
 *
 * @author Jay Xiong
 */
public class DefaultGeneralMap<K,V> extends HashMap<K,V> implements GeneralMap<K,V>{
    private static final long serialVersionUID = -8898499680831724783L;

    private final boolean sensitive;

    public DefaultGeneralMap() {
        this(true);
    }

    public DefaultGeneralMap(boolean sensitive) {
        this.sensitive = sensitive;
    }

    @Override
    public boolean isSensitive() {
        return sensitive;
    }

    @Override
    public V get(Object key) {
        //noinspection unchecked
        key = convertKey((K) key);
        return super.get(key);
    }

    @Override
    public V remove(Object key) {
        //noinspection unchecked
        key = convertKey((K) key);
        return super.remove(key);
    }

    @Override
    public V put(K key, V value) {
        key = convertKey(key);
        return super.put(key, value);
    }

    K convertKey(K key){
        if( key != null && key instanceof String && !sensitive){
            //noinspection unchecked
            return (K)key.toString().toLowerCase();
        }else return key;
    }

    @Override
    public String getString(K key) {
        V v = get(key);
        return v == null ? null : v.toString();
    }

    @Override
    public Boolean getBoolean(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).intValue() != 0 ;
        return v == null ? null : Boolean.getBoolean(v.toString().trim());
    }

    @Override
    public Integer getInteger(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).intValue();
        return v == null ? 0 : Integer.valueOf(v.toString().trim());
    }

    @Override
    public Short getShort(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).shortValue();
        return v == null ? 0 : Short.valueOf(v.toString().trim());
    }

    @Override
    public Byte getByte(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).byteValue();
        return v == null ? 0 : Byte.valueOf(v.toString().trim());
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
        return v == null ? 0 : Float.valueOf(v.toString().trim());
    }

    @Override
    public Double getDouble(K key) {
        V v = get(key);
        if( v instanceof Number) return ((Number) v).doubleValue();
        return v == null ? 0 : Double.valueOf(v.toString().trim());
    }

    public static <X, Y> DefaultGeneralMap<X, Y> parse(Map<X, Y> map) {
        DefaultGeneralMap<X, Y> result = new DefaultGeneralMap<X, Y>(false);
        for (X key : map.keySet()) {
            result.put(key, map.get(key));
        }
        return result;
    }


}
