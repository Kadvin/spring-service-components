/**
 * Developer: Kadvin Date: 14-2-14 上午11:08
 */
package net.happyonroad.redis;

import org.apache.commons.pool.impl.GenericObjectPool;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

/**
 * The customized redis pool for jedis naming
 */
class RedisPool extends Pool<Jedis> {

    public RedisPool(RedisConfig config) {
        super(config,
              new RedisFactory(
                      config.getHost(),
                      config.getPort(),
                      config.getTimeout(),
                      config.getPassword(),
                      config.getIndex()
              )
             );
    }


    @SuppressWarnings("UnusedDeclaration")
    public void returnBrokenResource(final BinaryJedis resource) {
        returnBrokenResourceObject(resource);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void returnResource(final BinaryJedis resource) {
        returnResourceObject(resource);
    }


    public GenericObjectPool<Jedis> getInternalPool(){
        return (GenericObjectPool<Jedis>)internalPool;
    }
}
