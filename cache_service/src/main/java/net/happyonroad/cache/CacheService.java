/**
 * @author XiongJie, Date: 13-10-24
 */

package net.happyonroad.cache;

/**
 * <h1>Cache服务的接口定义</h1>
 * <p/>
 */
public interface CacheService {
    /**
     * 获得一个Map的容器
     *
     * @param id the id
     * @return return the instance or null
     */
    MapContainer getMapContainer(final String id);

    /**
     * 在不取得Map对象引用的情况下，获取其中某个field的内容(字符形式)
     *
     * @param mapName Map 的名称
     * @param field   字段名称
     * @return Map中的值
     */
    String getMapString(final String mapName, final String field);

    /**
     * 在不取得Map对象引用的情况下，获取其中某个field的内容(二进制形式)
     *
     * @param mapName Map 的名称
     * @param field   字段名称
     * @return Map中的值
     */
    byte[] getMapBinary(final String mapName, final String field);

    /**
     * 获得一个List结构的容器
     *
     * @param id list 类型数据的标识
     * @return list 类型数据的访问接口
     */
    ListContainer getListContainer(final String id);

    /**
     * 获得一个数值容器
     *
     * @param id the id
     * @return return the instance or null
     */
    IntegerContainer getIntegerContainer(final String id);

    String get(final String key);

    void set(String key, String value);

    ////////////////////////////////////////////////////////////
    // Cache Listener Interfaces
    ////////////////////////////////////////////////////////////

    /**
     * 添加一个监听Cache事件的监听者
     *
     * @param listener 监听者
     */
    void addCacheListener(CacheListener listener);

    /**
     * 移除一个监听Cache 事件的监听者
     *
     * @param listener 监听者
     */
    void removeCacheListener(CacheListener listener);


    /**
     * 启动批量模式
     * @return 返回批量模式的资源
     */
    Object startBatch() throws Exception;

    /**
     * 结束批量模式
     * @param resource start batch所获得的key
     */
    void releaseBatch(Object resource);

}
