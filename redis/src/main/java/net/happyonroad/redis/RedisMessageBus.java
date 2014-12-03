/**
 * @author XiongJie, Date: 13-10-21
 */
package net.happyonroad.redis;

import net.happyonroad.messaging.MessageBus;
import net.happyonroad.messaging.MessageListener;
import net.happyonroad.util.NamedThreadFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.HashMap;
import java.util.Map;

/** Redis消息总线 */
@Component
public class RedisMessageBus implements MessageBus, ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(RedisMessageBus.class);

    private final Map<String, PubSubClient> listeners = new HashMap<String, PubSubClient>();

    private static NamedThreadFactory factory = new NamedThreadFactory("RedisQueue", "16K");

    private RedisCache cache;

    public RedisMessageBus() {
    }

    public RedisMessageBus(RedisCache cache) {
        this.cache = cache;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(this.cache == null){
            this.cache = applicationContext.getBean("redisCache", RedisCache.class);
        }
    }

    @Override
    public void publish(final String channel, final String event) {
        cache.withRunnable(new RedisRunnable() {
            public void run(Jedis jedis) {
                jedis.publish(channel, event);
            }
        });
    }

    @Override
    public void publish(final String channel, final byte[] event) {
        cache.withRunnable(new RedisRunnable() {
            public void run(Jedis jedis) {
                jedis.publish(channel.getBytes(), event);
            }
        });
    }

    @Override
    public void publishAll(final String channel, final String... events) {
        cache.withRunnable(new RedisRunnable() {
            public void run(Jedis jedis) {
                Pipeline pipeline = jedis.pipelined();
                for (String evt : events) {
                    pipeline.publish(channel, evt);
                }
                pipeline.syncAndReturnAll();
            }
        });
    }

    @Override
    public void publishAll(final String channel, final byte[]... events) {
        cache.withRunnable(new RedisRunnable() {
            public void run(Jedis jedis) {
                Pipeline pipeline = jedis.pipelined();
                for (byte[] evt : events) {
                    pipeline.publish(channel.getBytes(), evt);
                }
                pipeline.syncAndReturnAll();
            }
        });
    }

    @Override
    public void subscribe(String listenerId, String[] channels, MessageListener listener) {
        logger.info("Subscribe listener {} with id = `" + listenerId + "` on channels: {}",
                    listener.getClass().getSimpleName(),
                    StringUtils.join(channels, ","));
        PubSubClient client;
        if(listener.isBinary()){
            client = new BinaryPubSubClient(factory, cache, channels, listener);
        }else{
            client = new RedisPubSubClient(factory, cache, channels, listener);
        }
        listeners.put(listenerId, client);
    }

    @Override
    public void subscribe(String listenerId, String pattern, MessageListener listener) {
        logger.info("Subscribe listener {} with id = "+listenerId+" on channel with pattern: {}",
                    listener.getClass().getSimpleName(),
                    pattern);
        PubSubClient client;
        if(listener.isBinary()){
            client = new BinaryPubSubClient(cache, pattern, listener);
        }else{
            client = new RedisPubSubClient(cache, pattern, listener);
        }
        listeners.put(listenerId, client);
    }

    @Override
    public void unsubscribe(String listenerId) {
        PubSubClient client = listeners.get(listenerId);
        if (client != null ) {
            logger.info("Unsubscribe listener with id = {}",  listenerId);
            client.close();
        } else{
            logger.warn("Can't find subscribe with listener id = {}", listenerId);
        }
    }
}
