/**
 * @author XiongJie, Date: 13-10-21
 */

package net.happyonroad.messaging;

import java.util.EventListener;

/**
 * <h1>消息监听器</h1>
 * 事件回调接口
 */
public interface MessageListener extends EventListener{

    boolean isBinary();

    void onMessage(String channel, String message);

    void onMessage(String channel, byte[] message);

}
