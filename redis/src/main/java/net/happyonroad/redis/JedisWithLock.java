/**
 * Developer: Kadvin Date: 14-2-24 下午3:05
 */
package net.happyonroad.redis;

import redis.clients.jedis.Jedis;

import java.util.concurrent.locks.ReentrantLock;

/**
* A jedis with lock(stats)
*/
class JedisWithLock {

    private final Jedis         jedis;
    private final ReentrantLock lock;

    public JedisWithLock(Jedis jedis) {
        this.jedis = jedis;
        this.lock = new ReentrantLock();
    }

    void lock() {
        lock.lock();
    }

    void unlock() {
        lock.unlock();
    }

    boolean isLocked(){
        return lock.isLocked();
    }

    Jedis getJedis() {
        return jedis;
    }
}
