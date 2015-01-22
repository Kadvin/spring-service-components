/**
 * Developer: Kadvin Date: 15/1/16 上午9:16
 */
package net.happyonroad.platform.web.handler;

import net.happyonroad.platform.services.ServicePackagesEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <h1>能够将WebSocket Handler 任务委托出去</h1>
 */
public class DefaultDelegateWebSocketHandler implements
        DelegateWebSocketHandler, ApplicationListener<ServicePackagesEvent.LoadedEvent>{
    WebSocketHandler delegate;
    private boolean systemStarted;
    private Lock lock = new ReentrantLock(false);

    public DefaultDelegateWebSocketHandler() {
        lock.lock();
    }

    @Override
    public WebSocketHandler getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(WebSocketHandler concreteHandler) {
        this.delegate = concreteHandler;
    }

    //只有接收到这个消息之后才开始对外提供服务
    // 在此之前都block用户请求
    @Override
    public void onApplicationEvent(ServicePackagesEvent.LoadedEvent event) {
        this.systemStarted = true;
        lock.unlock();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        waitSystemStartedIfNeed();
        Assert.notNull(delegate);
        delegate.afterConnectionEstablished(session);
    }

    void waitSystemStartedIfNeed() {
        if( systemStarted ) return;
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            //unlocked
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        waitSystemStartedIfNeed();
        Assert.notNull(delegate);
        delegate.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        waitSystemStartedIfNeed();
        Assert.notNull(delegate);
        delegate.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        waitSystemStartedIfNeed();
        Assert.notNull(delegate);
        delegate.afterConnectionClosed(session, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        waitSystemStartedIfNeed();
        Assert.notNull(delegate);
        return delegate.supportsPartialMessages();
    }
}
