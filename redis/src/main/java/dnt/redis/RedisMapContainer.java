/**
 * @author XiongJie, Date: 13-10-21
 */
package dnt.redis;

import dnt.cache.MapContainer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.*;

/**
 * Redis Map Container
 */
@SuppressWarnings("unused")
public class RedisMapContainer implements MapContainer {

    private RedisCache cache;
    private final String id;

    /**
     * @param cache  the cache
     * @param id the map id
     */
    public RedisMapContainer(RedisCache cache, String id) {
        this.cache = cache;
        this.id = id;
    }

    public void put(final String key, final String serializable) {
        cache.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.hset(id, key, serializable);
            }
        });
    }

    @Override
    public void put(final String key, final byte[] bytes) {
        cache.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.hset(id.getBytes(), key.getBytes(), bytes);
            }
        });
    }

    public void putAll(final String[] keys, final String[] serializables) {
        if( keys.length == 0 )return;
        final Map<String, String> hash = new HashMap<String, String>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            String value = serializables[i];
            hash.put(key, value);
        }
        cache.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.hmset(id, hash);
            }
        });
    }

    public void putAll(final String[] keys, final byte[][] serializables) {
        if( keys.length == 0 )return;
        final Map<byte[], byte[]> hash = new HashMap<byte[], byte[]>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            byte[] value = serializables[i];
            hash.put(key.getBytes(), value);
        }
        cache.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.hmset(id.getBytes(), hash);
            }
        });
    }

    public boolean putIfNotExist(final String key, final String serializable) {
        return cache.withJedisCallable(new RedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                return 0 != jedis.hsetnx(id, key, serializable);
            }
        });
    }

    public String get(final String key) {
        return cache.withJedisCallable(new RedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                return jedis.hget(id, key);
            }
        });
    }

    @Override
    public byte[] getBinary(final String key) {
        return cache.withJedisCallable(new RedisCallable<byte[]>() {
            @Override
            public byte[] call(Jedis jedis) {
                return jedis.hget(id.getBytes(), key.getBytes());
            }
        });
    }

    public String[] getAll(final String... keys) {
        return cache.withJedisCallable(new RedisCallable<String[]>() {
            @Override
            public String[] call(Jedis jedis) {
                List<String> ret = jedis.hmget(id, keys);
                return ret.toArray(new String[ret.size()]);
            }
        });
    }

    public String[] getAll(Iterable<String> keys) {
        ArrayList<String> ss = new ArrayList<String>();
        for(String s : keys) {
            ss.add(s);
        }
        return getAll(ss.toArray(new String[ss.size()]));
    }

    public void remove(final String key) {
        cache.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.hdel(id, key);
            }
        });
    }

    public void removeAll(final Iterable<String> keys) {
          cache.withRunnable(new RedisRunnable() {
              @Override
              public void run(Jedis jedis) {
                  Pipeline pipeline = jedis.pipelined();
                  for (String s : keys) {
                      pipeline.hdel(id, s);
                  }
                  pipeline.syncAndReturnAll();
              }
          });
    }

    public void removeAll(final String... keys) {

          cache.withRunnable(new RedisRunnable() {
              @Override
              public void run(Jedis jedis) {
                  Pipeline pipeline = jedis.pipelined();
                  for (String s : keys) {
                      pipeline.hdel(id, s);
                  }
                  pipeline.syncAndReturnAll();
              }
          });
    }

    public String[] values() {
        return cache.withJedisCallable(new RedisCallable<String[]>() {
            @Override
            public String[] call(Jedis jedis) {
                List<String> result = jedis.hvals(id);
                return result.toArray(new String[result.size()]);
            }
        });
    }

    public String[] keys() {
        return cache.withJedisCallable(new RedisCallable<String[]>() {
            @Override
            public String[] call(Jedis jedis) {
                Set<String> result = jedis.hkeys(id);
                return result.toArray(new String[result.size()]);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Set<Map.Entry<String, String>> entrySet() {
        return cache.withJedisCallable(new RedisCallable<Set<Map.Entry<String, String>>>() {
            @Override
            public Set<Map.Entry<String, String>> call(Jedis jedis) {
                return jedis.hgetAll(id).entrySet();
            }
        });
    }

    public long size() {
        return cache.withJedisCallable(new RedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.hlen(id);
            }
        });
    }

    public void clear() {
          cache.withRunnable(new RedisRunnable() {
              @Override
              public void run(Jedis jedis) {
                  jedis.del(id);
              }
          });
    }
}
