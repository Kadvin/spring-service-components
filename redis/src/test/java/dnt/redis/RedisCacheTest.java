/**
 * Developer: Kadvin Date: 14-9-28 下午2:40
 */
package dnt.redis;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Description here
 */
@ContextConfiguration(locations = "classpath:META-INF/application.xml")
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore("It needs local redis")
public class RedisCacheTest {
    @Autowired
    RedisCache cache;

    @Before
    public void setUp() throws Exception {
        System.setProperty("redis.host", "localhost");
        System.setProperty("redis.port", "6379");
        cache.start();
    }

    @After
    public void tearDown() throws Exception {
        cache.stop();
    }

    @Test
    public void testConfiguration() throws Exception {
        cache.set("hello", "world");
        Assert.assertEquals("world", cache.get("hello"));
    }
}
