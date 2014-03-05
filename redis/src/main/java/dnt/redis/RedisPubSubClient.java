/**
 * @author XiongJie, Date: 13-10-21
 */
package dnt.redis;

import dnt.messaging.MessageListener;
import dnt.util.NamedThreadFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.SocketException;

/**
 * <h2>Redis 消息接收器</h2>
 * 监听多个通道，并将相应的消息转发给指定Message Listener的对象
 */
class RedisPubSubClient extends JedisPubSub implements Runnable, PubSubClient {
    protected static Logger logger = LoggerFactory.getLogger(RedisPubSubClient.class);
    private final String[]        channels;
    private final String          pattern;
    private final MessageListener listener;
    private boolean isStopped = false;
    private RedisCache cache;

    public RedisPubSubClient(NamedThreadFactory factory, RedisCache cache,
                             String[] channels,
                             MessageListener listener) {
        this.cache = cache;
        this.channels = channels;
        this.pattern = null;
        this.listener = listener;

        Thread thread = factory.newThread(this);
        thread.setName("RedisQueue-" + StringUtils.join(channels, ","));
        thread.setDaemon(true);
        thread.start();
    }

    public RedisPubSubClient(RedisCache cache,
                             String channelPattern,
                             MessageListener listener) {
        this.cache = cache;
        this.channels = null;
        this.pattern = channelPattern;
        this.listener = listener;

        Thread thread = new Thread(this, "RedisQueue-" + pattern);
        thread.setDaemon(true);
        thread.start();
    }

    public void close() {
        try {
            isStopped = true;
            this.unsubscribe();
        } catch (JedisConnectionException e) {
            //do nothing when error
            logger.warn("RedisPubSubClient close failed!", e);
        } catch (Exception e) {
            //Sometimes, redis.clients.jedis.JedisPubSub.unsubscribe(JedisPubSub.java:34) will throw NullPointerException
            //when exe starts
            logger.warn("RedisPubSubClient close failed!", e);
        }
    }

    @Override
    public void run() {
        while (!isStopped) {
            Jedis jedis = null;
            try {
                //不应该基于RedisUsage，因为这个subscribe连接不能与其他场景复用
                jedis = cache.getPool().getResource();
                String name = jedis.clientGetname();
                if (channels != null)
                {
                    jedis.clientSetname(name + "-Subscribe-" + StringUtils.join(channels, ","));
                    jedis.subscribe(this, channels);
                }
                else
                {
                    jedis.clientSetname(name + "-PSubscribe-" + pattern);
                    jedis.psubscribe(this, pattern);
                }
                cache.getPool().returnResource(jedis);
            } catch (Exception e) {
                if (e.getCause() instanceof SocketException) {
                    String channel;
                    if (channels != null)
                        channel = StringUtils.join(channels, ",");
                    else
                        channel = pattern;
                    cache.getPool().returnBrokenResource(jedis);
                    logger.warn(listener + " disconnected from: " + channel, e);
                } else {
                    logger.error(e.getMessage(), e);
                }
            }

            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e) {
                /** ignore */
            }
        }
    }

    @Override
    public void onMessage(String channel, String message) {
        try {
            if (message != null) {
                listener.onMessage(channel, message);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        onMessage(channel, message);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        logger.info("Pattern {} subscribe callback with channel id = {}", pattern, subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        logger.info("Pattern {} unsubscribe with channel id {}!", pattern, subscribedChannels);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        logger.info("Subscribe to channel {} with channel id = {}", channel, subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        logger.info("Unsubscribe channel {} with id {}!", channel, subscribedChannels);
    }
}
