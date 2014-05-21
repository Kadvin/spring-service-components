/**
 * @author XiongJie, Date: 14-1-13
 */
package dnt.model;

import net.minidev.json.JSONAware;

/** A single ip range*/
public class SingleIp extends IpRange implements JSONAware{

    private final String ip;

    public SingleIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean include(String ip) {
        return this.ip.equals(ip);
    }

    @Override
    public String toJSONString() {
        return ip;
    }

    public String getAddress() {
        return ip;
    }

    @Override
    public String asParam() {
        return getAddress();
    }
}
