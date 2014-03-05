/**
 * @author XiongJie, Date: 13-10-24
 */
package dnt.cache;

import java.util.Map;
import java.util.Set;

/**
 * Cache中 Map 类型数据的访问接口
 * <p/>
 * 主要用于在不将整个Map获取到当前进程内时进行操作
 * <p>各个API的含义，可参考Redis的Map Commands</p>
 */
public interface MapContainer {

    /**
     * 向Cache中的这个Map中放内容
     *
     * @param key          the key
     * @param serializable the value
     */
    void put(final String key, final String serializable);

    void put(final String key, final byte[] bytes);

    byte[] getBinary(final String key);

    /**
     * 向Cache中的这个Map中放一批对应的内容
     *
     * @param keys          the key array
     * @param serializables the value array
     */
    void putAll(final String[] keys, final String[] serializables);

    /**
     * 向Cache中的这个Map中放一批对应的内容
     *
     * @param keys          the key array
     * @param serializables the value array
     */
    void putAll(final String[] keys, final byte[][] serializables);

    /**
     * 从Map中获取内容
     *
     * @param key the key
     * @return return the value or null
     */
    String get(final String key);



    /**
     * 从Map中获取给定Key的所有内容
     *
     * @param keys the key
     * @return return the value or null
     */
    String[] getAll(final Iterable<String> keys);


    /**
     * 从Map中获取给定Key的所有内容
     *
     * @param keys the key
     * @return return the value or null
     */
    String[] getAll(final String... keys);

    /**
     * 从Map中删除特定内容
     *
     * @param key the key
     */
    void remove(final String key);

    /**
     * 从Map中删除给定的所有内容
     *
     * @param keys the key
     */
    void removeAll(final Iterable<String> keys);

    /**
     * 从Map中删除给定的所有内容
     *
     * @param keys the key
     */
    void removeAll(final String... keys);

    /**
     * 对Map的内容进行迭代
     *
     * @return return all key/value pair
     */
    Set<Map.Entry<String, String>> entrySet();

    /**
     * 在特定内容不存在的时候，向Map中设置值
     *
     * @return return true if not exist.
     */
    boolean putIfNotExist(final String key, final String serializable);


    /**
     * 导出所有值
     *
     * @return return all value
     */
    String[] values();

    /**
     * 导出所有Key
     *
     * @return return all key
     */
    String[] keys();

    /**
     * 得到Map的大小
     *
     * @return return size
     */
    long size();

    /** 清空Map的内容 */
    void clear();
}
