/**
 * @author XiongJie, Date: 13-10-25
 */
package net.happyonroad.cache.support;

import net.happyonroad.cache.*;
import net.happyonroad.spring.Bean;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.*;

/** 最简单的Cache实现 */
@Component
@ManagedResource(objectName = "net.happyonroad:type=service,name=defaultCache")
class DefaultCache extends Bean implements MutableCacheService {
    /* 某个jvm内存中共享的实例 */
    private static DefaultCache shared;

    Map<String, IntegerContainer> intContainers  = new HashMap<String, IntegerContainer>();
    Map<String, MapContainer>     mapContainers  = new HashMap<String, MapContainer>();
    Map<String, ListContainer>    listContainers = new HashMap<String, ListContainer>();
    Map<String, String>           values         = new HashMap<String, String>();
    Set<CacheListener>            listeners      = new HashSet<CacheListener>();

    public DefaultCache() {
        shared = this;

    }

    @Override
    public MapContainer getMapContainer(String id) {
        MapContainer ret = mapContainers.get(id);
        if (ret == null) {
            ret = new DefaultMapContainer();
            mapContainers.put(id, ret);
        }
        return ret;
    }

    @Override
    public ListContainer getListContainer(String id) {
        ListContainer ret = listContainers.get(id);
        if (ret == null) {
            ret = new DefaultListContainer();
            listContainers.put(id, ret);
        }
        return ret;
    }

    void removeListContainer(String name) {
        listContainers.remove(name);
    }


    @Override
    public IntegerContainer getIntegerContainer(String id) {
        IntegerContainer ret = intContainers.get(id);
        if (ret == null) {
            ret = new DefaultIntegerContainer();
            intContainers.put(id, ret);
        }
        return ret;
    }

    @Override
    public String get(String key) {
        return this.values.get(key);
    }

    @Override
    public void set(String key, String value) {
        this.values.put(key, value);
    }

    @Override
    public String getMapString(String mapName, String field) {
        return getMapContainer(mapName).get(field);
    }

    @Override
    public byte[] getMapBinary(String mapName, String field) {
        return getMapContainer(mapName).getBinary(field);
    }

    ////////////////////////////////////////////////
    // Mutable Cache Service
    ////////////////////////////////////////////////

    @Override
    public byte[] blockPopRightFromList(String name, int timeout) {
        logger.trace("Pop from {} max timeout: {}s", name, timeout);
        ListContainer container = getListContainer(name);
        try {
            return container.blockPopRight(timeout);
        } finally {
            //Avoid memory leak
            if( container.size() == 0){
                removeListContainer(name);
            }
        }
    }


    @Override
    public void pushLeftToList(String name, byte[] value) {
        logger.trace("Push to {} with {}", name, value);
        getListContainer(name).pushLeft(value);
    }

    /**
     * 非单件模式
     *
     * @return 在单个进程内获取共享的内存cache实例
     */
    public static DefaultCache getSharedInstance() {
        if (shared == null) {
            shared = new DefaultCache();
        }
        return shared;
    }

    @Override
    public void addCacheListener(CacheListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeCacheListener(CacheListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Object startBatch() {
        return new Object();
    }

    @Override
    public void releaseBatch(Object resource) {

    }

    @Override
    public void connect(CacheClient client) {
        for (CacheListener listener : listeners) {
            try{
                listener.whenClientConnected(client);
            }catch (Exception ex){
                logger.warn(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void disconnect(CacheClient client) {
        for (CacheListener listener : listeners) {
            try{
                listener.whenClientDisconnected(client);
            }catch (Exception ex){
                logger.warn(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void keepAlive(CacheClient client) {
        for (CacheListener listener : listeners) {
            try{
                listener.whenClientKeepAlive(client);
            }catch (Exception ex){
                logger.warn(ex.getMessage(), ex);
            }
        }
    }
}
