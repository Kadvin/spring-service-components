/**
 * Developer: Kadvin Date: 14-1-22 下午5:30
 */
package net.happyonroad.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Description here
 */
public class PatternUtilsTest {
    @Test
    public void testCompileForPath() throws Exception {
        String raw = "path/to/*";
        Assert.assertTrue(PatternUtils.compile(raw).matcher("path/to/any").find());
        Assert.assertTrue(PatternUtils.compile(raw).matcher("path/to/any/path").find());
    }

    @Test
    public void testCompileForType() throws Exception {
        String raw = "oracle*";
        Assert.assertTrue(PatternUtils.compile(raw).matcher("oracle server").find());
        Assert.assertFalse(PatternUtils.compile(raw).matcher("server ora").matches());
    }

    @Test
    public void testCompileForNormal() throws Exception {
        String raw = "oracle";
        Assert.assertTrue(PatternUtils.compile(raw).matcher("oracle").matches());
        Assert.assertFalse(PatternUtils.compile(raw).matcher(" oracle ").matches());
    }


}
