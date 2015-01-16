/**
 * Developer: Kadvin Date: 15/1/16 上午9:16
 */
package net.happyonroad.platform.web.handler;

import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * <h1>能够将WebSocket Handler 任务委托出去</h1>
 */
public class DefaultDelegateWebSocketHandler implements DelegateWebSocketHandler {
    WebSocketHandler delegate;

    @Override
    public WebSocketHandler getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(WebSocketHandler concreteHandler) {
        this.delegate = concreteHandler;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Assert.notNull(delegate);
        delegate.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Assert.notNull(delegate);
        delegate.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Assert.notNull(delegate);
        delegate.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Assert.notNull(delegate);
        delegate.afterConnectionClosed(session, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        Assert.notNull(delegate);
        return delegate.supportsPartialMessages();
    }
}
