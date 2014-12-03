/**
 * @author XiongJie, Date: 13-11-11
 */
package net.happyonroad.cache;

import java.util.EventListener;

/** Cache Event Listener */
public interface CacheListener extends EventListener {
    /**
     * 当某个客户端连上时，该调用得到回调
     *
     * @param client 客户端
     */
    void whenClientConnected(CacheClient client);

    /**
     * 当某个客户端断开后，该调用得到回调
     *
     * @param client 客户端
     */
    void whenClientDisconnected(CacheClient client);

    /**
     * 当某个客户端发起心跳通知
     * @param client 客户端
     */
    void whenClientKeepAlive(CacheClient client);
}
