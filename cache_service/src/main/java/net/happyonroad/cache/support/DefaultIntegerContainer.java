/**
 * @author XiongJie, Date: 13-10-25
 */
package net.happyonroad.cache.support;

import net.happyonroad.cache.IntegerContainer;

import java.util.*;

class DefaultIntegerContainer implements IntegerContainer {

    Map<String, Long> values = new HashMap<String, Long>();

    @Override
    public synchronized void set(String key, long serializable) {
        values.put(key, serializable);
    }

    @Override
    public synchronized long get(String key) {
        Long v = values.get(key);
        return (null == v)?-1L:v;
    }

    @Override
    public synchronized long[] getAll(Iterable<String> keys) {
        ArrayList<Long> result = new ArrayList<Long>();
        for (String key : keys) {
            result.add(values.get(key));
        }
        return toArray(result);
    }

    private long[] toArray(Collection<Long> values) {
        long[] result = new long[values.size()];
        int i = 0;
        for(Long v : values) {
            result[i ++] = v;
        }
        return result;
    }

    @Override
    public synchronized long[] getAll(String... keys) {
        return getAll(keys);
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
    public synchronized Set<Map.Entry<String, Long>> entrySet() {
        return values.entrySet();
    }

    @Override
    public synchronized long[] values() {
        return toArray(values.values());
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

    @Override
    public synchronized long addAndGet(String key, long add) {
        Long v = values.get(key);
        if(null == v) {
            v = add;
        } else {
            v += add;
        }
        values.put(key, v);
        return v;
    }

    @Override
    public long[] addAndGetAll(Map<String, Long> values) {
        for(Map.Entry<String, Long> entry : values.entrySet()) {
            addAndGet(entry.getKey(), entry.getValue());
        }
        return new long[0];
    }
}
