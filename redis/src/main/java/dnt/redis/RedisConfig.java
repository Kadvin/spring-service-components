/**
 * Developer: Kadvin Date: 14-2-12 下午9:24
 */
package dnt.redis;

import dnt.util.TimeInterval;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.context.Lifecycle;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;
import java.util.Set;

/**
 * Redis configuration
 */
@Component
public class RedisConfig extends JedisPoolConfig implements Lifecycle {
    ////////////////////////////////////////////////////////////////////////////////////
    //静态成员变量
    ////////////////////////////////////////////////////////////////////////////////////
    public static int     DEFAULT_MAX_ACTIVE                        = 10;
    public static int     DEFAULT_MAX_IDLE                          = 5;
    public static long    DEFAULT_MAX_WAIT                          = 1000 * 60;
    public static boolean DEFAULT_TEST_ON_BORROW                    = true;
    public static boolean DEFAULT_TEST_ON_RETURN                    = false;
    public static boolean DEFAULT_TEST_WHILE_IDLE                   = true;
    public static long    DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS    = 1000 * 60 * 5;//5 minutes
    public static long    DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 100 * 60 * 3;// 3 minutes
    public static int     DEFAULT_NUM_TESTS_PER_EVICTION_RUN        = -1;

    public static String DEFAULT_HOST    = "localhost";
    public static String DEFAULT_PORT    = "6379";
    public static String DEFAULT_INDEX    = "0";
    public static String DEFAULT_TIMEOUT = "10s";

    private String  host;
    private int     port;
    private String  password;
    private int     index;
    private int     timeout;

    private boolean running;

    ////////////////////////////////////////////////////////////////////////////////////
    // 构造函数
    ////////////////////////////////////////////////////////////////////////////////////

    public RedisConfig() {
        super();
        //当某个Key下面的资源不足时，应该采取的动作
        // block: 等待; grow: 增长(这将会突破MaxActive限制); fail：抛出异常
        //当资源不够用时，严格按照 Max Active的限制，不越雷池半步，调用者会一直block到超时(maxWaitTime)
        setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        //以下是对资源池基本行为的控制
        setMaxActive(DEFAULT_MAX_ACTIVE);//资源池中每个key下面最大多少个活跃的资源
        setMaxIdle(DEFAULT_MAX_IDLE);    //资源池中每个Key下面最大多少个备选的资源
        setMaxWait(DEFAULT_MAX_WAIT);    //向资源池借资源时，最大超时时间
        //以下是对资源的测试设置
        setTestOnBorrow(DEFAULT_TEST_ON_BORROW);     //借出来资源之后，要不要先验证一下
        setTestOnReturn(DEFAULT_TEST_ON_RETURN);     //还回资源之后，要不要验证下
        setTestWhileIdle(DEFAULT_TEST_WHILE_IDLE);   //空闲的资源要不要时不时测试下，对于有些面向连接的资源，如果不想底层连接被超时释放掉，可以通过这个机制保持心跳
        //以下是关于资源清理的设置
        setMinEvictableIdleTimeMillis(DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);      //一个资源经过多少时间不被使用就会被视为无效？
        setTimeBetweenEvictionRunsMillis(
                DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);//清理Idle资源的线程应该每隔多少毫秒清理一遍（如果需要测试idle资源，也顺便测试下）
        setNumTestsPerEvictionRun(DEFAULT_NUM_TESTS_PER_EVICTION_RUN); //清理idle资源的线程每轮最多清理多少个
    }

    @ManagedAttribute
    public String getHost() {
        return host;
    }

    @ManagedAttribute
    public int getPort() {
        return port;
    }

    @ManagedAttribute
    public int getTimeout() {
        return timeout;
    }

    @ManagedAttribute
    public String getPassword() {
        return password;
    }

    @ManagedAttribute
    public int getIndex() {
        return index;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void start() {
        apply(System.getProperties());
        running = true;
    }

    @Override
    public void stop() {
        running = false;//do nothing
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // Getter/Setter as managed attribute
    ////////////////////////////////////////////////////////////////////////////////////


    @ManagedAttribute
    @Override
    public int getMaxIdle() {
        return super.getMaxIdle();
    }

    @ManagedAttribute
    @Override
    public void setMaxIdle(int maxIdle) {
        super.setMaxIdle(maxIdle);
    }

    @ManagedAttribute
    @Override
    public int getMinIdle() {
        return super.getMinIdle();
    }

    @ManagedAttribute
    @Override
    public void setMinIdle(int minIdle) {
        super.setMinIdle(minIdle);
    }

    @ManagedAttribute
    @Override
    public int getMaxActive() {
        return super.getMaxActive();
    }

    @ManagedAttribute
    @Override
    public void setMaxActive(int maxActive) {
        super.setMaxActive(maxActive);
    }

    @ManagedAttribute
    @Override
    public long getMaxWait() {
        return super.getMaxWait();
    }

    @ManagedAttribute
    @Override
    public void setMaxWait(long maxWait) {
        super.setMaxWait(maxWait);
    }

    @ManagedAttribute
    @Override
    public byte getWhenExhaustedAction() {
        return super.getWhenExhaustedAction();
    }

    @ManagedAttribute
    @Override
    public void setWhenExhaustedAction(byte whenExhaustedAction) {
        super.setWhenExhaustedAction(whenExhaustedAction);
    }

    @ManagedAttribute
    @Override
    public boolean isTestOnBorrow() {
        return super.isTestOnBorrow();
    }

    @ManagedAttribute
    @Override
    public void setTestOnBorrow(boolean testOnBorrow) {
        super.setTestOnBorrow(testOnBorrow);
    }

    @ManagedAttribute
    @Override
    public boolean isTestOnReturn() {
        return super.isTestOnReturn();
    }

    @ManagedAttribute
    @Override
    public void setTestOnReturn(boolean testOnReturn) {
        super.setTestOnReturn(testOnReturn);
    }

    @ManagedAttribute
    @Override
    public boolean isTestWhileIdle() {
        return super.isTestWhileIdle();
    }

    @ManagedAttribute
    @Override
    public void setTestWhileIdle(boolean testWhileIdle) {
        super.setTestWhileIdle(testWhileIdle);
    }

    @ManagedAttribute
    @Override
    public long getTimeBetweenEvictionRunsMillis() {
        return super.getTimeBetweenEvictionRunsMillis();
    }

    @ManagedAttribute
    @Override
    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        super.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    @ManagedAttribute
    @Override
    public int getNumTestsPerEvictionRun() {
        return super.getNumTestsPerEvictionRun();
    }

    @ManagedAttribute
    @Override
    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        super.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }

    @ManagedAttribute
    @Override
    public long getMinEvictableIdleTimeMillis() {
        return super.getMinEvictableIdleTimeMillis();
    }

    @ManagedAttribute
    @Override
    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        super.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    @ManagedAttribute
    @Override
    public long getSoftMinEvictableIdleTimeMillis() {
        return super.getSoftMinEvictableIdleTimeMillis();
    }

    @ManagedAttribute
    @Override
    public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        super.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
    }

    /**
     * 本类识别以下配置项：
     * <ul>
     * <li>maxActive</li>
     * <li>maxIdle</li>
     * <li>maxWait</li>
     * <li>testOnBorrow</li>
     * <li>testOnReturn</li>
     * <li>testWhileIdle</li>
     * <li>minEvictableIdleTime</li>
     * <li>timeBetweenEvictionRuns</li>
     * <li>numTestsPerEvictionRun</li>
     * </ul>
     *
     * @param configurations 配置项
     */
    public void apply(Properties configurations) {
        host = configurations.getProperty("redis.host", DEFAULT_HOST);
        port = Integer.valueOf(configurations.getProperty("redis.port", DEFAULT_PORT));
        password = configurations.getProperty("redis.password");
        index = Integer.valueOf(configurations.getProperty("redis.index", DEFAULT_INDEX));
        timeout = TimeInterval.parseInt(configurations.getProperty("redis.timeout", DEFAULT_TIMEOUT));

        Set<String> strings = configurations.stringPropertyNames();
        for (String property : strings) {
            if (!property.startsWith("redis.")) continue;
            if (property.endsWith("maxActive")) {
                int maxActive = TimeInterval.parseInt(configurations.getProperty("redis.maxActive"));
                setMaxActive(maxActive);

            } else if (property.endsWith("maxIdle")) {
                int maxIdle = Integer.valueOf(configurations.getProperty("redis.maxIdle"));
                setMaxIdle(maxIdle);

            } else if (property.endsWith("maxWait")) {
                int maxWait = Integer.valueOf(configurations.getProperty("redis.maxWait"));
                setMaxWait(maxWait);

            } else if (property.endsWith("testOnBorrow")) {
                boolean testOnBorrow = Boolean.valueOf(configurations.getProperty("redis.testOnBorrow"));
                setTestOnBorrow(testOnBorrow);

            } else if (property.endsWith("testOnReturn")) {
                boolean testOnReturn = Boolean.valueOf(configurations.getProperty("redis.testOnReturn"));
                setTestOnReturn(testOnReturn);

            } else if (property.endsWith("testWhileIdle")) {
                boolean testWhileIdle = Boolean.valueOf(configurations.getProperty("redis.testWhileIdle"));
                setTestWhileIdle(testWhileIdle);

            } else if (property.endsWith("minEvictableIdleTime")) {
                long minEvictableIdleTime =
                        new TimeInterval(configurations.getProperty("redis.minEvictableIdleTime")).getMilliseconds();
                setMinEvictableIdleTimeMillis(minEvictableIdleTime);

            } else if (property.endsWith("timeBetweenEvictionRuns")) {
                long timeBetweenEvictionRuns =
                        new TimeInterval(configurations.getProperty("redis.timeBetweenEvictionRuns")).getMilliseconds();
                setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRuns);

            } else if (property.endsWith("numTestsPerEvictionRun")) {
                int numTestsPerEvictionRun =
                        Integer.valueOf(configurations.getProperty("redis.numTestsPerEvictionRun"));
                setNumTestsPerEvictionRun(numTestsPerEvictionRun);

            }
        }
    }

}
