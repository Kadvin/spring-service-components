/**
 * Developer: Kadvin Date: 15/1/16 上午9:13
 */
package net.happyonroad.platform.web.handler;

import org.springframework.web.socket.WebSocketHandler;

/**
 * <h1>将任务委托给其他真实Web Socket Handler的委托者接口</h1>
 */
public interface DelegateWebSocketHandler extends WebSocketHandler {

    void setDelegate(WebSocketHandler concreteHandler);

    WebSocketHandler getDelegate();
}
