/**
 * @author XiongJie, Date: 13-10-21
 */

package dnt.redis;

import redis.clients.jedis.Jedis;

/** Runnable in jedis support */
public interface RedisRunnable {
    void run(Jedis jedis);
}
