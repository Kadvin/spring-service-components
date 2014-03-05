/**
 * @author XiongJie, Date: 14-1-13
 */
package dnt.model;

/** A single ip range*/
public class SingleIp extends IpRange {

    private final String ip;

    public SingleIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean include(String ip) {
        return this.ip.equals(ip);
    }
}
