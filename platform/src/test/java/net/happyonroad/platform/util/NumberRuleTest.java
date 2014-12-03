/**
 * Developer: Kadvin Date: 14/11/5 上午10:08
 */
package net.happyonroad.platform.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * The number rule test
 */
public class NumberRuleTest {

    @Test
    public void testParseSimple() throws Exception {
        NumberRule rule = NumberRule.parse("TEST_%d");
        Assert.assertEquals("TEST_%d", rule.getFormat());
        Assert.assertEquals(1L, rule.getStart());
        Assert.assertEquals(1, rule.getIncrement());
    }

    @Test
    public void testParseWithStart() throws Exception {
        NumberRule rule = NumberRule.parse("TEST_%06d@1000");
        Assert.assertEquals("TEST_%06d", rule.getFormat());
        Assert.assertEquals(1000L, rule.getStart());
        Assert.assertEquals(1, rule.getIncrement());
    }

    @Test
    public void testParseWithIncrement() throws Exception {
        NumberRule rule = NumberRule.parse("TEST_%06d/2");
        Assert.assertEquals("TEST_%06d", rule.getFormat());
        Assert.assertEquals(1L, rule.getStart());
        Assert.assertEquals(2, rule.getIncrement());
    }

    @Test
    public void testParseWithStartAndIncrement() throws Exception {
        NumberRule rule = NumberRule.parse("TEST_%06d@1000/10");
        Assert.assertEquals("TEST_%06d", rule.getFormat());
        Assert.assertEquals(1000L, rule.getStart());
        Assert.assertEquals(10, rule.getIncrement());
    }
}
