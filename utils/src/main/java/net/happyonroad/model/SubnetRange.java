/**
 * @author XiongJie, Date: 14-1-13
 */
package net.happyonroad.model;

import org.apache.commons.net.util.SubnetUtils;

/**
 * The subnetwork range
 */
public class SubnetRange extends IpRange{

    private final SubnetUtils.SubnetInfo info;

    /**
     * 构建一个子网对象
     *
     * @param subnet 子网ip
     * @param mask   掩码
     */
    public SubnetRange(String subnet, String mask) {
        this.info = new SubnetUtils(subnet, mask).getInfo();
    }

    /**
     * 将 192.168.10.0 这种格式的子网按照默认的策略转换为Range对象
     * 所谓的默认策略，就是 .0 转换为 8位， .0.0转换为16位
     *
     * @param range ip范围
     */
    public SubnetRange(String range) {
        String[] dots = range.split("\\.");
        if (dots.length != 4) throw new IllegalArgumentException("Wrong range format " + range);
        int mask = 32;
        for (int i = 3; i > 0; i--) {
            int value = Integer.parseInt(dots[i]);
            if (value != 0) break;
            mask -= 8;
        }
        if (mask == 0) throw new IllegalArgumentException("The ip should end with one .0 at least");
        this.info = new SubnetUtils(range + "/" + mask).getInfo();
    }

    @Override
    public boolean include(String ip) {
        return info.isInRange(ip);
    }

    public String getAddress(){
        return info.getAddress();
    }

    public String getMask(){
        return info.getNetmask();
    }

    @Override
    public String asParam() {
        return regular(info.getAddress());
    }
}
