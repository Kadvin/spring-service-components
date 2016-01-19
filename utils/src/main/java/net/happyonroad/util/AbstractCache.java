package net.happyonroad.util;

import net.happyonroad.spring.ApplicationSupportBean;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>抽象的对象缓存</h1>
 *
 * @author Jay Xiong
 */
public abstract class AbstractCache<K, V> extends ApplicationSupportBean {
    // 对象缓存
    private final Map<K, V> objectCache;
    // 对象类型
    private final Class<V>  objectClass;

    public AbstractCache(Class<V> objectClass) {
        this.objectCache = new ConcurrentHashMap<K, V>();
        this.objectClass = objectClass;
    }

    public Class<V> getObjectClass() {
        return objectClass;
    }

    protected abstract K parseKey(V value);

    /**
     * <h2>从cache中直接查找对象实例</h2>
     *
     * @param key 对象的key
     * @return 找到的实例
     */
    protected V findInCache(K key) {
        return objectCache.get(key);
    }

    /**
     * <h2>根据特定条件查找对象</h2>
     *
     * @param filter 过滤条件
     * @return 找到的对象
     */
    protected V findInCache(Predicate<V> filter) {
        for (V value : cachedValues()) {
            if (filter.evaluate(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * <h2>根据特定条件查找所有符合条件的对象</h2>
     *
     * @param filter 过滤条件
     * @return 找到的对象集合
     */
    protected List<V> findAllInCache(Predicate<V> filter) {
        List<V> result = new LinkedList<V>();
        for (V value : cachedValues()) {
            if (filter.evaluate(value)) {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * <h2>在object cache中缓存对象</h2>
     *
     * @param key   被缓存的对象key
     * @param value 被缓存的对象实例
     */
    public void cache(K key, V value) {
        if( key == null){
            logger.error("Can't cache null for value " + value);
            return;
        }
        objectCache.put(key, value);
    }

    /**
     * <h2>获取当前缓存的所有对象实例</h2>
     *
     * @return 对象实例集合
     */
    protected Collection<V> cachedValues() {
        return objectCache.values();
    }

    /**
     * <h2>获取当前缓存的所有对象Key</h2>
     *
     * @return 对象Key集合
     */
    protected Collection<K> cachedKeys() {
        return objectCache.keySet();
    }

    protected void innerAdd(V value) {
        K key = parseKey(value);
        cache(key, value);
    }

    protected void innerUpdate(V value) {
        K key = parseKey(value);
        cache(key, value);
    }

    protected void innerRemove(V value) {
        K key = parseKey(value);
        uncache(key);
    }

    public void uncache(K key){
        objectCache.remove(key);
    }

    protected void purgeCache() {
        objectCache.clear();
    }

    @ManagedAttribute(description = "当前Cache存储的数量")
    public int getSize() {
        return objectCache.size();
    }
}
