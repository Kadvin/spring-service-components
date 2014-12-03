/**
 * @author XiongJie, Date: 13-10-21
 */
package net.happyonroad.messaging;

/** 消息总线接口 */
public interface MessageBus {
    void publish(String channel, String event);

    void publish(String channel, byte[] event);

    void publishAll(String channel,  String... events);

    void publishAll(String channel,  byte[]... events);

    void subscribe(String listenerId, String[] channels, MessageListener listener);

    void subscribe(String listenerId, String pattern, MessageListener listener);

    void unsubscribe(String listenerId);
}
