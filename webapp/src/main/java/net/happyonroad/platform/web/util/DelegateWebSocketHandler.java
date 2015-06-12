/**
 * Developer: Kadvin Date: 15/1/16 上午9:16
 */
package net.happyonroad.platform.web.util;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * <h1>能够将WebSocket Handler 任务委托出去</h1>
 */
public class DelegateWebSocketHandler extends BlockingDelegator<WebSocketHandler> implements WebSocketHandler{


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        waitDelegateIfNeed();
        delegate.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        waitDelegateIfNeed();
        delegate.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        waitDelegateIfNeed();
        delegate.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        waitDelegateIfNeed();
        delegate.afterConnectionClosed(session, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        waitDelegateIfNeed();
        return delegate.supportsPartialMessages();
    }
}
