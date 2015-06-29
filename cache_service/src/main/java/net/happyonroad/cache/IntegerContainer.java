/**
 * @author XiongJie, Date: 13-10-24
 */
package net.happyonroad.cache;

import java.util.Map;
import java.util.Set;

/**
 * 数值的容器
 */
public interface IntegerContainer {

    /**
     * add value with the specific key to cache
     *
     * @param key the key
     * @param add the value
     * @return return the value or null
     */
    long addAndGet(final String key, final long add);

    /**
     * add value with the specific key to cache
     *
     * @param values the key
     * @return return the all value or null
     */
    long[] addAndGetAll(Map<String, Long> values);

    /**
     * set a value with the specific key to cache
     *
     * @param key the key
     * @param value the value
     */
    void set(final String key, long value);

    /**
     * get a value with the specific key from cache
     *
     * @param key the key
     * @return return the value or null
     */
    long get(final String key);


    /**
     * get all value with the specific key from cache
     *
     * @param keys the key
     * @return return the value or null
     */
    long[] getAll(final Iterable<String> keys);


    /**
     * get all value with the specific key from cache
     *
     * @param keys the key
     * @return return the value or null
     */
    long[] getAll(final String... keys);

    /**
     * remove a value with the specific key from cache
     *
     * @param key the key
     */
    void remove(final String key);

    /**
     * remove a value with the specific key from cache
     *
     * @param keys the key
     */
    void removeAll(final Iterable<String> keys);

    /**
     * remove a value with the specific key from cache
     *
     * @param keys the key
     */
    void removeAll(final String... keys);

    /**
     * list all key/value pair from cache
     *
     * @return return all key/value pair
     */
    Set<Map.Entry<String, Long>> entrySet();

    /**
     * list all value from cache
     *
     * @return return all vslue
     */
    long[] values();

    /**
     * list all key from cache
     *
     * @return return all key
     */
    String[] keys();

    /**
     * get size from cache
     *
     * @return return size
     */
    long size();

    /**
     * clear all key/value from cache
     */
    void clear();
}