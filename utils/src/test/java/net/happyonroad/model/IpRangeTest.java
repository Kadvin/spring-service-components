/**
 * Developer: Kadvin Date: 14-5-14 下午7:41
 */
package net.happyonroad.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the subnet range
 */
public class IpRangeTest {
    @Test
    public void testParseSubnetRange() {
        IpRange[] ranges = IpRange.parse("192.168.12.0/24");
        Assert.assertTrue(ranges[0] instanceof SubnetRange);
        SubnetRange range = (SubnetRange) ranges[0];
        Assert.assertEquals("192.168.12.0", range.getAddress());
        Assert.assertEquals("255.255.255.0", range.getMask());
    }

    @Test
    public void testParseSingleIpRange() {
        IpRange[] ranges = IpRange.parse("192.168.12.21");
        Assert.assertTrue(ranges[0] instanceof SingleIp);
        Assert.assertEquals("192.168.12.21", ((SingleIp) ranges[0]).getAddress());
    }

    @Test
    public void testParseStartAndEndRange() {
        IpRange[] ranges = IpRange.parse("192.168.12.21-42");
        Assert.assertTrue(ranges[0] instanceof StartAndEndRange);
        StartAndEndRange range = (StartAndEndRange) ranges[0];
        Assert.assertEquals("192.168.12.21", range.getStart());
        Assert.assertEquals("192.168.12.42", range.getEnd());
    }

    @Test
    public void testParseStartAndEndRange2() {
        IpRange[] ranges = IpRange.parse("192.168.10-12.21-42");
        Assert.assertTrue(ranges[0] instanceof StartAndEndRange);
        StartAndEndRange range = (StartAndEndRange) ranges[0];
        Assert.assertEquals("192.168.10.21", range.getStart());
        Assert.assertEquals("192.168.12.42", range.getEnd());
    }

    @Test
    public void testParseCombinedRange() {
        IpRange[] ranges = IpRange.parse("192.168.12.0/24 192.168.12.21 192.168.12.21-42");
        Assert.assertTrue(ranges[0] instanceof SubnetRange);
        Assert.assertTrue(ranges[1] instanceof SingleIp);
        Assert.assertTrue(ranges[2] instanceof StartAndEndRange);
    }

    @Test
    public void testSubnetRangeToAndFromJson() throws Exception {
        IpRange[] ranges = IpRange.parse("192.168.12.0/24");
        SubnetRange range = (SubnetRange) ranges[0];
        String json = range.toJson();
        System.out.println(json);
        SubnetRange newRange = SubnetRange.parseJson(json, SubnetRange.class);
        Assert.assertEquals(json, newRange.toJson());
    }

    @Test
    public void testSingleIpToAndFromJson() throws Exception {
        IpRange[] ranges = IpRange.parse("192.168.12.21");
        SingleIp range = (SingleIp) ranges[0];
        String json = range.toJson();
        System.out.println(json);
        SingleIp newRange = SingleIp.parseJson(json, SingleIp.class);
        Assert.assertEquals(json, newRange.toJson());
    }


    @Test
    public void testStartAndEndRangeToAndFromJson() throws Exception {
        IpRange[] ranges = IpRange.parse("192.168.12.21-42");
        StartAndEndRange range = (StartAndEndRange) ranges[0];
        String json = range.toJson();
        System.out.println(json);
        StartAndEndRange newRange = StartAndEndRange.parseJson(json, StartAndEndRange.class);
        Assert.assertEquals(json, newRange.toJson());
    }


}
