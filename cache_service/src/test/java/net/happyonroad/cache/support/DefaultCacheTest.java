/**
 * @author XiongJie, Date: 13-10-28
 */
package net.happyonroad.cache.support;

import net.happyonroad.cache.CacheService;
import net.happyonroad.cache.IntegerContainer;
import net.happyonroad.cache.ListContainer;
import net.happyonroad.cache.MapContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** 测试默认的内存内的Cache是否可用 */
public class DefaultCacheTest {
    //面向Cache Service进行测试，暂不测试其内部方法
    private CacheService cache;

    @Before
    public void setUp() throws Exception {
        cache = new DefaultCache();
    }

    /**
     * 测试目的：
     *   测试默认Cache中没有数据
     * 验证方式：
     *   没有异常
     *   取得null
     * @throws Exception
     */
    @Test
    public void testGet() throws Exception {
        Assert.assertNull(cache.get("the/key"));
    }

    /**
     * 测试目的：
     *   测试向默认Cache中存放数据
     * 验证方式：
     *   没有异常
     *   取得的和存入的数据一致
     * @throws Exception
     */
    @Test
    public void testSet() throws Exception {
        cache.set("the/key", "the value");
        Assert.assertEquals("the value", cache.get("the/key"));
    }

    /**
     * 测试目的：
     *   能够基于Cache构建一个List Container对象
     * 验证方式：
     *   获取到的List Container对象，且数据长度为0
     * @throws Exception
     */
    @Test
    public void testGetListContainer() throws Exception {
        ListContainer container = cache.getListContainer("the/list/container");
        Assert.assertNotNull(container);
        Assert.assertEquals(0, container.size());
    }

    /**
     * 测试目的：
     *   能够基于Cache构建一个Map Container对象
     * 验证方式：
     *   获取到的Map Container对象，且数据长度为0
     * @throws Exception
     */
    @Test
    public void testGetMapContainer() throws Exception {
        MapContainer container = cache.getMapContainer("the/map/container");
        Assert.assertNotNull(container);
        Assert.assertEquals(0, container.size());
    }

    /**
     * 测试目的：
     *   能够直接从Cache的Map中获取字符对象
     * 验证方式：
     *   获取到的字符对象与原先放入的一样
     * @throws Exception
     */
    @Test
    public void testGetMapString() throws Exception {
        MapContainer container = cache.getMapContainer("the/map/container");
        container.put("the/map/key", "the/map/value");
        Assert.assertEquals("the/map/value", cache.getMapString("the/map/container", "the/map/key"));
    }

    /**
     * 测试目的：
     *   能够基于Cache构建一个Integer Container对象
     * 验证方式：
     *   获取到的Integer Container对象，且数据长度为0
     * @throws Exception
     */
    @Test
    public void testGetIntegerContainer() throws Exception {
        IntegerContainer container = cache.getIntegerContainer("the/integer/container");
        Assert.assertNotNull(container);
        Assert.assertEquals(0, container.size());
    }
}
