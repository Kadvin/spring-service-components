/**
 * @author XiongJie, Date: 13-10-21
 */
package net.happyonroad.redis;

import net.happyonroad.cache.ListContainer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;

/**
 * Redis List container
 */
@SuppressWarnings("unused")
class RedisListContainer implements ListContainer {

    private final String id;
    private RedisCache cache;

    /**
     * @param cache the Jedis cache.
     * @param id the container name
     */
    public RedisListContainer(RedisCache cache, String id) {
        this.cache = cache;
        this.id = id;
    }

    public String popLeft() {
        return cache.withJedisCallable(new RedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                return jedis.lpop(id);
            }
        });
    }

    public String popRight() {
        return cache.withJedisCallable(new RedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                return jedis.rpop(id);
            }
        });
    }

    public String blockPopLeft(final int timeout) {
        return cache.withJedisCallable(new RedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                List<String> ret = jedis.blpop(timeout, id);
                return (null == ret) ? null : ret.get(1);
            }
        });
    }

    @Override
    public byte[] blockPopRight(final int timeout) {
        return cache.withJedisCallable(new RedisCallable<byte[]>() {
            @Override
            public byte[] call(Jedis jedis) {
                // returns a two elements list: key + value
                List<byte[]> list = jedis.brpop(timeout, id.getBytes());
                if(list != null ) return list.get(1);
                return null;// means timeout
            }
        });
    }

    public void pushLeft(final byte[] value) {
        cache.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.lpush(id.getBytes(), value);
            }
        });
    }

    public void pushRight(final String value) {
        cache.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.rpush(id, value);
            }
        });
    }

    @Override
    public void pushLeft(final String value) {
        cache.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.lpush(id, value);
            }
        });
    }

    public void pushRight(final List<String> values) {
        cache.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                Pipeline pipeline = jedis.pipelined();
                for (String value : values) {
                    jedis.rpush(id, value);
                }
                pipeline.syncAndReturnAll();
            }
        });
    }

    public boolean trim(final int start, final int end) {
        return cache.withJedisCallable(new RedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                String ret = jedis.ltrim(id, start, end);
                return "OK".equalsIgnoreCase(ret);
            }
        });
    }

    public String[] toArray() {
        return cache.withJedisCallable(new RedisCallable<String[]>() {
            @Override
            public String[] call(Jedis jedis) {
                List<String> ret = jedis.lrange(id, 0, -1);
                return (null == ret) ? new String[0] : ret
                        .toArray(new String[ret.size()]);
            }
        });
    }

    public String[] subList(final int start, final int end) {
        return cache.withJedisCallable(new RedisCallable<String[]>() {
            @Override
            public String[] call(Jedis jedis) {
                if( start == end ) return new String[0];
                int endPos = end;
                if( endPos > 0 ) endPos--;
                List<String> ret = jedis.lrange(id, start, endPos);
                return (null == ret) ? new String[0] : ret
                        .toArray(new String[ret.size()]);
            }
        });
    }

    public int size() {
        return cache.withJedisCallable(new RedisCallable<Integer>() {
            @Override
            public Integer call(Jedis jedis) {
                return jedis.llen(id).intValue();
            }
        });
    }
}
