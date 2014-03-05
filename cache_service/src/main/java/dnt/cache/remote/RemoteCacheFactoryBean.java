/**
 * @author XiongJie, Date: 13-11-11
 */
package dnt.cache.remote;

import dnt.cache.MutableCacheService;
import dnt.cache.support.DefaultCacheClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import java.net.ConnectException;
import java.rmi.RemoteException;

/**
 * 与服务器的连接对象
 * 通过传统的RMI与服务器连接，而后再将其中的Cache封装成为底层通讯通道
 */
public class RemoteCacheFactoryBean extends RmiProxyFactoryBean
        implements DisposableBean, ApplicationEventPublisherAware, Runnable {
    public static final int UNIT = 500;

    private Logger logger        = LoggerFactory.getLogger(getClass());
    private Thread connectKeeper = new Thread(this, "ConnectionKeeper");
    private DefaultCacheClient        myself;
    private String                    clientId;
    private String                    clientName;
    private ApplicationEventPublisher publisher;
    private boolean                   running;
    private boolean                   connected;
    private int interval = 1;

    @Override
    public void afterPropertiesSet() {
        myself = new DefaultCacheClient(clientId);
        myself.setName(clientName);
        this.running = true;
        connectKeeper.start();
    }

    @Override
    public void destroy() throws Exception {
        this.running = false;
        try {
            connectKeeper.interrupt();
        } catch (Exception e) {
            //skip it
        } finally {
            disconnect();
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setContextClassLoader(getBeanClassLoader());
        while (running) {
            if (connected) {
                keepAlive();
            } else {
                connect();
            }
            try {
                Thread.sleep(UNIT * interval);
            } catch (InterruptedException e) {
                //ignore
            }
            //Thread.yield();
        }
    }

    protected MutableCacheService getCacheService() {
        return (MutableCacheService) getObject();
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    private void connect() {
        try {
            super.afterPropertiesSet();
            success();
            logger.info("Connected with the server: {} by remote cache as: {}", getServiceUrl(), myself);
        } catch (Exception ex) {
            fail();
            logger.warn("Error " + ex.getMessage() + " while try to connect to: " + getServiceUrl());
            return;
        }
        try {
            getCacheService().connect(myself);
            // 局部事件就通过Spring的Context走
            //  就是接收者与发送者在同一个上下文中时的事件，认为是局部事件
            //  当双方无法通过上下文通讯时，应该走Message Bus
            publisher.publishEvent(new RemoteCacheConnectedEvent(getCacheService()));
            logger.debug("Notify the server connected");
            success();
        } catch (Exception ex) {
            //don't throw any exception
            //skip it
            logger.error("Failed to notify server connected: " + myself, ex);
            fail();
        } finally {
            connected = true;
        }
    }

    private void disconnect() {
        try {
            getCacheService().disconnect(myself);
            success();
            logger.debug("Notify the server disconnected");
        } catch (Exception e) {
            fail();
            logger.warn("Error " + e.getMessage() + " while try to disconnect from: " + getServiceUrl());
        } finally {
            publisher.publishEvent(new RemoteCacheDisconnectedEvent(getCacheService()));
            connected = false;
        }
    }

    private void keepAlive() {
        try {
            getCacheService().keepAlive(myself);
            success();
            logger.trace("Notify the server I'm alive");
        } catch (Exception e) {
            fail();
            if( isConnectionFailed(e) ){
                connectLost(e);
            }
            logger.warn("Error " + e.getMessage() + " while send keep alive signal to: " + getServiceUrl());
        }
    }

    private boolean isConnectionFailed(Throwable e) {
        if(e == null) return false;
        if( e instanceof ConnectException){
            return true;
        }else if (e instanceof RemoteException){
            return isConnectFailure((RemoteException)e);
        }else {
            return isConnectionFailed(e.getCause());
        }
    }

    private void connectLost(Exception e) {
        logger.trace("Connection is broken because of: " + e.getMessage());
        if( connected ){
            connected = false;
            try {
                publisher.publishEvent(new RemoteCacheDisconnectedEvent(getCacheService()));
            } catch (Exception e1) {
                logger.debug(e1.getMessage());
            }
        }
    }

    private void fail() {
        interval *= 2;
        if (interval > 256) interval = 1;
    }

    private void success() {
        interval = 1;
    }
}
