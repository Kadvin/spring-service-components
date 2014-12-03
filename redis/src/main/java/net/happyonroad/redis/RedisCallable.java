/**
 * @author XiongJie, Date: 13-10-21
 */

package net.happyonroad.redis;

import redis.clients.jedis.Jedis;

/** Callable with Redis support */
public interface RedisCallable<T> {
    T call(Jedis jedis);
}
