/**
 * @author XiongJie, Date: 13-10-25
 */
package net.happyonroad.cache.support;

import net.happyonroad.cache.MapContainer;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class DefaultMapContainer implements MapContainer {

    Map<String, byte[]> values = new HashMap<String, byte[]>();

    @Override
    public synchronized void put(String key, String serializable) {
        values.put(key, serializable.getBytes());
    }

    @Override
    public void put(String key, byte[] bytes) {
        values.put(key, bytes);
    }

    @Override
    public synchronized void putAll(String[] keys, String[] serializables) {
        Validate.isTrue(keys.length == serializables.length, "keys.length != serializables.length");
        for (int i = 0; i < keys.length; ++i) {
            values.put(keys[i], serializables[i].getBytes());
        }
    }

    @Override
    public synchronized void putAll(String[] keys, byte[][] serializables) {
        Validate.isTrue(keys.length == serializables.length, "keys.length != serializables.length");
        for (int i = 0; i < keys.length; ++i) {
            values.put(keys[i], serializables[i]);
        }
    }

    @Override
    public synchronized String get(String key) {
        return asString(values.get(key));
    }

    @Override
    public byte[] getBinary(String key) {
        return values.get(key);
    }

    @Override
    public synchronized String[] getAll(Iterable<String> keys) {
        ArrayList<String> result = new ArrayList<String>();
        for (String key : keys) {
            result.add(asString(values.get(key)));
        }
        return result.toArray(new String[result.size()]);
    }

    @Override
    public synchronized String[] getAll(String... keys) {
        return new String[0];
    }

    @Override
    public synchronized void remove(String key) {
        values.remove(key);
    }

    @Override
    public synchronized void removeAll(Iterable<String> keys) {
        for (String key : keys) {
            values.remove(key);
        }
    }

    @Override
    public synchronized void removeAll(String... keys) {
        for (String key : keys) {
            values.remove(key);
        }
    }

    @Override
    public synchronized Set<Map.Entry<String, String>> entrySet() {
        return toStringMap(values).entrySet();
    }

    @Override
    public synchronized boolean putIfNotExist(String key, String serializable) {
        if (this.values.containsKey(key)) {
            return false;
        }
        this.values.put(key, serializable.getBytes());
        return true;
    }

    @Override
    public synchronized String[] values() {
        return toStringMap(values).values().toArray(new String[values.values().size()]);
    }

    @Override
    public synchronized String[] keys() {
        return values.keySet().toArray(new String[values.keySet().size()]);
    }

    @Override
    public synchronized long size() {
        return values.size();
    }

    @Override
    public synchronized void clear() {
         values.clear();
    }


    private String asString(byte[] bytes) {
        if(bytes == null)return null;
        return new String(bytes);
    }

    private byte[] toBytes(String string){
        if(string == null)return null;
        return string.getBytes();
    }

    private Map<String, byte[]> toBytesMap(Map<String, String> values) {
        Map<String, byte[]> result = new HashMap<String, byte[]>(values.size());
        for (String key : values.keySet()) {
            result.put(key, toBytes(values.get(key)));
        }
        return result;
    }

    private Map<String, String> toStringMap(Map<String, byte[]> values) {
        Map<String, String> result = new HashMap<String, String>(values.size());
        for (String key : values.keySet()) {
            result.put(key, asString(values.get(key)));
        }
        return result;
    }

}
