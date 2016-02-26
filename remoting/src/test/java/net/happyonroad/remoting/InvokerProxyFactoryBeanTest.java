/**
 * @author XiongJie, Date: 13-11-4
 */
package net.happyonroad.remoting;

import net.happyonroad.cache.CacheService;
import net.happyonroad.cache.remote.RemoteCacheConnectedEvent;
import net.happyonroad.cache.remote.RemoteCacheDisconnectedEvent;
import net.happyonroad.test.user.DemoRemoteServiceUser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/** 通过消息服务进行调用反转的测试 */
@ContextConfiguration(locations = "classpath:service-import-sample.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class InvokerProxyFactoryBeanTest {
    @Autowired
    private DemoRemoteServiceUser     user;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private CacheService              cacheService;

    @Before
    public void setUp() throws Exception {
        publisher.publishEvent(new RemoteCacheConnectedEvent(cacheService));
    }

    @After
    public void tearDown() throws Exception {
        publisher.publishEvent(new RemoteCacheDisconnectedEvent(cacheService));
    }

    /**
     * 测试目的：
     *   测试可以通过InvokerProxyFactoryBean将远程服务作为一个Spring Bean暴露出来
     * 验证方式：
     *   <ul>
     *     <li>Spring Context存在对应对象</li>
     *     <li>对InvokerProxyFactoryBean提供出来的对象调用，将会导致远程对象被调用</li>
     *   </ul>
     * @throws Exception
     */
    @Test
    public void testImportRemoteService() throws Exception {
        Assert.assertNotNull(user.getRemoteService());
        String effort = user.work();
        Assert.assertEquals("Hello {DNT}", effort);
    }
}
