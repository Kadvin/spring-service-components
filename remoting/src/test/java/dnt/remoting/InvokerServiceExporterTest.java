/**
 * @author XiongJie, Date: 13-11-4
 */
package dnt.remoting;

import dnt.cache.CacheService;
import dnt.cache.remote.RemoteCacheConnectedEvent;
import dnt.cache.remote.RemoteCacheDisconnectedEvent;
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
@ContextConfiguration(locations = "classpath:service-export-sample.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class InvokerServiceExporterTest {
    @Autowired
    private InvokerServiceExporter    exporter;
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
     * 测试可以通过InvokeServiceExporter将本地服务作为一个Spring Bean代理暴露出来
     * 验证方式：
     * <ul>
     * <li>Spring Context存在对应对象</li>
     * <li>对暴露出来的服务对象的调用将会导致实际对象被调用</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test
    public void testExportRemoteService() throws Exception {
        Assert.assertNotNull(exporter);
        Assert.assertNotNull(exporter.getProxy());
    }
}
