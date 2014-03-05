/**
 * @author XiongJie, Date: 13-11-11
 */

package dnt.cache;

/**
 * 可以操作的Cache Service
 * 这与RedisCache有所不同，主要是对内存内Cache的封装
 */
public interface MutableCacheService extends CacheService {

    /**
     * 当客户端连上时，主动通知其
     *
     * @param client 客户端
     */
    void connect(CacheClient client);

    /**
     * 客户端断开前，主动通知其
     *
     * @param client 客户端
     */
    void disconnect(CacheClient client);

    /**
     * 保持活跃
     *
     * @param client 客户端
     */
    void keepAlive(CacheClient client) throws Exception;

    /**
     * 封装了ListChannel#blockPopRight()
     *
     * @param name  List Name
     * @param timeout Timeout
     * @return value
     */
    byte[] blockPopRightFromList(String name, int timeout);


    /**
     * 封装了ListChannel#pushLeft
     *
     * @param name  List Name
     * @param value value
     */
    void pushLeftToList(String name, byte[] value);
}
