/**
 * @author XiongJie, Date: 13-10-21
 */
package net.happyonroad.redis;

import net.happyonroad.cache.CacheClient;
import net.happyonroad.cache.CacheListener;
import net.happyonroad.cache.MutableCacheService;
import net.happyonroad.spring.Bean;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Redis Cache对象
 *
 */
@Component
@ManagedResource(objectName = "net.happyonroad:type=service,name=redisCache")
class RedisCache extends Bean implements MutableCacheService {
    protected static Logger logger = LoggerFactory.getLogger(RedisCache.class);

    private RedisConfig config;

    private RedisPool             pool;
    private ThreadLocal<JedisWithLock> resources;
    private Set<CacheListener> listeners;


    public RedisCache() {
        this(new RedisConfig());
    }

    public RedisCache(RedisConfig config) {
        this.config = config;
        this.resources = new ThreadLocal<JedisWithLock>();
        this.listeners = new HashSet<CacheListener>();
    }

    ////////////////////////////////////////////////
    // Lifecycle
    ////////////////////////////////////////////////

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void performStart() {
        super.performStart();
        pool = new RedisPool(config);
        ensureConnected();
    }

    private void ensureConnected() {
        Jedis resource = null;
        while(isRunning() && resource == null){
            try {
                logger.info("Connecting to {}: {}:{}/{}",
                            config.getRedisName(), config.getHost(), config.getPort(), config.getIndex());
                resource = pool.getResource();
                logger.info("Connected  to {}: {}:{}/{}",
                            config.getRedisName(), config.getHost(), config.getPort(), config.getIndex());
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    //skip
                }
                logger.error(ExceptionUtils.getRootCauseMessage(e));
            }
        }
        if( resource != null ) pool.returnResource(resource);
    }

    @Override
    protected void performStop() {
        //正常关闭之前，要求redis主动进行保存
        withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                try {
                    jedis.save();
                } catch (Exception e) {
                    logger.warn("Failed to save redis before stop: {} ", ExceptionUtils.getRootCauseMessage(e));
                }
            }
        });
        pool.destroy();
    }

    ////////////////////////////////////////////////
    // Basic Interface
    ////////////////////////////////////////////////

    public RedisMapContainer getMapContainer(final String id) {
        return new RedisMapContainer(this, id);
    }

    public RedisIntegerContainer getIntegerContainer(String id) {
        return new RedisIntegerContainer(this, id);
    }

    public void set(final String key, final String value) {
        this.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.set(key, value);
            }
        });
    }

    public String get(final String key) {
        return this.withJedisCallable(new RedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    public byte[] getMapBinary(final String mapName, final String field) {
        return this.withJedisCallable(new RedisCallable<byte[]>() {
            @Override
            public byte[] call(Jedis jedis) {
                return jedis.hget(mapName.getBytes(), field.getBytes());
            }
        });
    }

    public RedisListContainer getListContainer(String id) {
        return new RedisListContainer(this, id);
    }


    @Override
    public String getMapString(final String mapName, final String field) {
        return this.withJedisCallable(new RedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                return jedis.hget(mapName, field);
            }
        });
    }

    @Override
    public void pexpire(final String key, final int milliseconds) {
        this.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.pexpire(key, milliseconds);
            }
        });
    }

    ////////////////////////////////////////////////
    // Mutable Cache Service
    ////////////////////////////////////////////////

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
    public void keepAlive(CacheClient client) throws Exception {
        for (CacheListener listener : listeners) {
            try{
                listener.whenClientKeepAlive(client);
            }catch (Exception ex){
                logger.warn(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public byte[] blockPopRightFromList(final String name, final int timeout) {
        return this.withJedisCallable(new RedisCallable<byte[]>() {
            @Override
            public byte[] call(Jedis jedis) {
                // returns a two elements list: key + value
                List<byte[]> list = jedis.brpop(timeout, name.getBytes());
                if(list != null ) return list.get(1);
                return null;// means timeout
            }
        });
    }


    @Override
    public void pushLeftToList(final String name, final byte[] value) {
        this.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.lpush(name.getBytes(), value);
            }
        });
    }



    ////////////////////////////////////////////////
    // Internal Impl
    ////////////////////////////////////////////////

    void withRunnable(RedisRunnable user) {
        Jedis jedis = acquire();
        try {
            user.run(jedis);
        } catch (JedisConnectionException e) {
            logger.error(String.format("%s broken: %s", jedis, e.getMessage()), e);
            throw e;
        } finally {
            release(jedis);
        }
    }

    <T> T withJedisCallable(RedisCallable<T> caller) {
        Jedis jedis = acquire();
        try {
            return caller.call(jedis);
        } catch (JedisConnectionException e) {
            logger.error(String.format("%s broken: %s", jedis, e.getMessage()), e);
            throw e;
        } finally {
            release(jedis);
        }
    }

    public Pool<Jedis> getPool() {
        return pool;
    }

    //
    // 在线程上下文中复用同一个Redis实例
    //
    //  使用可重入锁来实现基于线程的 jedis 复用
    //
    public Jedis acquire() {
        JedisWithLock jedisWithLock = resources.get();
        if(jedisWithLock == null){
            Jedis jedis = pool.getResource();
            jedisWithLock = new JedisWithLock(jedis);
            resources.set(jedisWithLock);
        }
        jedisWithLock.lock();
        return jedisWithLock.getJedis();
    }

    public void release(Jedis jedis) {
        JedisWithLock jedisWithLock = resources.get();
        if( jedisWithLock == null ){
            pool.returnBrokenResource(jedis);
            return;
        }
        jedisWithLock.unlock();

        if(!jedisWithLock.isLocked()){
            pool.returnResource(jedisWithLock.getJedis());
            //不再搞 returnBrokenResource，需要配置 testOnReturn, testOnBorrow
            resources.set(null);
        }
    }

    ////////////////////////////////////////////////
    // Batch Related
    ////////////////////////////////////////////////

    @Override
    public Jedis startBatch() {
        return acquire();
    }

    @Override
    public void releaseBatch(Object resource) {
        release((Jedis) resource);
    }


    ////////////////////////////////////////////////
    // Cache Listener Interfaces
    ////////////////////////////////////////////////

    @Override
    public void addCacheListener(CacheListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeCacheListener(CacheListener listener) {
        listeners.remove(listener);
    }

    ////////////////////////////////////////////////
    // JMX support
    ////////////////////////////////////////////////

    @ManagedAttribute
    public int getNumActive() {
        return pool.getInternalPool().getNumActive();
    }

    @ManagedAttribute
    public int getNumIdle() {
        return pool.getInternalPool().getNumIdle();
    }

    @ManagedAttribute
    public int getMaxActive() {
        return pool.getInternalPool().getMaxActive();
    }

    @ManagedAttribute
    public void setMaxActive(int number) {
        pool.getInternalPool().setMaxActive(number);
    }

    @ManagedAttribute
    public int getMaxIdle() {
        return pool.getInternalPool().getMaxIdle();
    }

    @ManagedAttribute
    public void setMaxIdle(int number) {
        pool.getInternalPool().setMaxIdle(number);
    }

    @ManagedAttribute
    public int getMinIdle() {
        return pool.getInternalPool().getMinIdle();
    }

    @ManagedAttribute
    public void setMinIdle(int number) {
        pool.getInternalPool().setMinIdle(number);
    }

    @ManagedAttribute
    public int getNumTestsPerEvictionRun() {
        return pool.getInternalPool().getNumTestsPerEvictionRun();
    }

    @ManagedAttribute
    public void setNumTestsPerEvictionRun(int number) {
        pool.getInternalPool().setNumTestsPerEvictionRun(number);
    }

    @ManagedAttribute
    public byte getWhenExhaustedAction() {
        return pool.getInternalPool().getWhenExhaustedAction();
    }

    @ManagedAttribute
    public void setWhenExhaustedAction(byte number) {
        pool.getInternalPool().setWhenExhaustedAction(number);
    }

    @ManagedAttribute
    public long getTimeBetweenEvictionRunsMillis() {
        return pool.getInternalPool().getTimeBetweenEvictionRunsMillis();
    }

    @ManagedAttribute
    public void setTimeBetweenEvictionRunsMillis(long number) {
        pool.getInternalPool().setTimeBetweenEvictionRunsMillis(number);
    }

    @ManagedAttribute
    public long getMaxWait() {
        return pool.getInternalPool().getMaxWait();
    }

    @ManagedAttribute
    public void setMaxWait(long number) {
        pool.getInternalPool().setMaxWait(number);
    }

    @ManagedAttribute
    public long getMinEvictableIdleTimeMillis() {
        return pool.getInternalPool().getMinEvictableIdleTimeMillis();
    }

    @ManagedAttribute
    public void setMinEvictableIdleTimeMillis(long number) {
        pool.getInternalPool().setMinEvictableIdleTimeMillis(number);
    }

    @ManagedAttribute
    public long getSoftMinEvictableIdleTimeMillis() {
        return pool.getInternalPool().getSoftMinEvictableIdleTimeMillis();
    }

    @ManagedAttribute
    public void setSoftMinEvictableIdleTimeMillis(long number) {
        pool.getInternalPool().setSoftMinEvictableIdleTimeMillis(number);
    }

    @ManagedAttribute
    public boolean getLifo() {
        return pool.getInternalPool().getLifo();
    }

    @ManagedAttribute
    public void setLifo(boolean bool) {
        pool.getInternalPool().setLifo(bool);
    }

    @ManagedAttribute
    public boolean getTestOnBorrow() {
        return pool.getInternalPool().getTestOnBorrow();
    }

    @ManagedAttribute
    public void setTestOnBorrow(boolean bool) {
        pool.getInternalPool().setTestOnBorrow(bool);
    }

    @ManagedAttribute
    public boolean getTestOnReturn() {
        return pool.getInternalPool().getTestOnReturn();
    }

    @ManagedAttribute
    public void setTestOnReturn(boolean bool) {
        pool.getInternalPool().setTestOnReturn(bool);
    }

    @ManagedAttribute
    public boolean getTestWhileIdle() {
        return pool.getInternalPool().getTestWhileIdle();
    }

    @ManagedAttribute
    public void setTestWhileIdle(boolean bool) {
        pool.getInternalPool().setTestWhileIdle(bool);
    }


}
