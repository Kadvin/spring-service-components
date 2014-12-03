/**
 * @author XiongJie, Date: 13-10-21
 */
package net.happyonroad.redis;

import net.happyonroad.cache.IntegerContainer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.*;

/**
 * Redis integer container
 */
@SuppressWarnings("unused")
public class RedisIntegerContainer implements IntegerContainer {

    private RedisCache cache;
    private final String id;

    /**
     * @param cache  the cache
     * @param id the map id
     */
    public RedisIntegerContainer(RedisCache cache, String id) {
        this.cache = cache;
        this.id = id;
    }

    public void set(final String key, final long serializable) {
        cache.withRunnable(new RedisRunnable() {
            @Override
            public void run(Jedis jedis) {
                jedis.hset(id, key, Long.toString(serializable));
            }
        });
    }

    public long get(final String key) {
        return cache.withJedisCallable(new RedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                String v = jedis.hget(id, key);
                if(null == v || 0 == v.length()) {
                    return -1L;
                }
                return Long.parseLong(v);
            }
        });
    }

    long[] toArray(List<String> values) {
        int i = 0;
        long[] result = new long[values.size()];
        for(String v : values) {
            result[i ++] = Long.parseLong(v);
        }
        return result;
    }

    public long[] getAll(final String... keys) {
        return cache.withJedisCallable(new RedisCallable<long[]>() {
            @Override
            public long[] call(Jedis jedis) {
                List<String> ret = jedis.hmget(id, keys);
                return toArray(ret);
            }
        });
    }

    public long[] getAll(Iterable<String> keys) {
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

    public long[] values() {
        return cache.withJedisCallable(new RedisCallable<long[]>() {
            @Override
            public long[] call(Jedis jedis) {
                List<String> result = jedis.hvals(id);
                return toArray(result);
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
    public Set<Map.Entry<String, Long>> entrySet() {
        return cache.withJedisCallable(new RedisCallable<Set<Map.Entry<String, Long>>>() {
            @Override
            public Set<Map.Entry<String, Long>> call(Jedis jedis) {
                Set<Map.Entry<String, Long>> result = new HashSet<Map.Entry<String, Long>>();

                for(Map.Entry<String, String> entry : jedis.hgetAll(id).entrySet()){
                    result.add(new Entry(entry.getKey(), entry.getValue()));
                }
                return result;
            }
        });
    }
                      
    public long addAndGet(final String key, final long add) {
        return cache.withJedisCallable(new RedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.hincrBy(id, key, add);
            }
        });
    }

    public long[] addAndGetAll(final Map<String,Long> values) {
        return cache.withJedisCallable(new RedisCallable<long[]>() {
            @Override
            public long[] call(Jedis jedis) {
                Pipeline pipeline = jedis.pipelined();
                for(Map.Entry<String,Long> s : values.entrySet()) {
                    pipeline.hincrBy(id, s.getKey(), s.getValue());
                }
                List r = pipeline.syncAndReturnAll();
                long[] result = new long[r.size()];
                int i = 0;
                for(Object o : r){
                    result[i ++] = (null == o)?0:(Long)o;
                }
                return result;
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

    public static class Entry implements Map.Entry<String, Long> {

        String key;
        String value;

        public  Entry(String key, String value){
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Long getValue() {
            return Long.valueOf(value);
        }

        @Override
        public Long setValue(Long value) {
            throw new UnsupportedOperationException();
        }
    }
}
