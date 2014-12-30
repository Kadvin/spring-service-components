/**
 * Developer: Kadvin Date: 14-6-13 下午3:21
 */
package net.happyonroad.support;

import junit.framework.Assert;
import net.happyonroad.util.ParseUtils;
import org.junit.Test;

/**
 * Test against inet address wrapper
 */
public class DefaultHostAddressTest {
    private String JSON =  "{\"host\":\"127.0.0.1\"}";

    @Test
    public void testToJson() throws Exception {
        String json = new DefaultHostAddress("127.0.0.1").toJson();
        Assert.assertEquals(JSON, json);
    }

    @Test
    public void testFromJson() throws Exception {
        DefaultHostAddress address = ParseUtils.parseJson(JSON, DefaultHostAddress.class);
        Assert.assertEquals("127.0.0.1", address.getHost());
    }
}
