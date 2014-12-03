/**
 * Developer: Kadvin Date: 14-6-13 下午3:23
 */
package net.happyonroad.support;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Test against inet socket address wrapper
 */
public class DefaultSocketAddressTest {
    private String JSON =  "{\"host\":\"127.0.0.1\",\"port\":1096}";

    @Test
    public void testToJson() throws Exception {
        String json = new DefaultSocketAddress("127.0.0.1", 1096).toJson();
        Assert.assertEquals(JSON, json);
    }

    @Test
    public void testFromJson() throws Exception {
        DefaultSocketAddress address = DefaultSocketAddress.parseJson(JSON, DefaultSocketAddress.class);
        Assert.assertEquals("127.0.0.1", address.getHost());
        Assert.assertEquals(1096, address.getPort());
    }
}
