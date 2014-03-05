/**
 * @author XiongJie, Date: 14-1-13
 */

package dnt.model;

/**
 * Judge a ip in the range or not
 */
public abstract class IpRange {
    public abstract boolean include(String ip);

    public static IpRange[] parse(String exclusiveIps) {
        String[] rawRanges = exclusiveIps.split(",\\s*");
        IpRange[] ranges = new IpRange[rawRanges.length];
        for (int i = 0; i < rawRanges.length; i++) {
            String rawRange = rawRanges[i];
            if (rawRange.indexOf('-') > 0) {
                //ip range: 192.168.0.10-192.168.0.20
                String[] split = rawRange.split("-");
                ranges[i] = new StartAndEndRange(split[0], split[1]);
            } else if (rawRange.indexOf('/') > 0) {
                //subnetwork: 192.168.0.0/255.255.0.0
                String[] split = rawRange.split("/");
                ranges[i] = new SubnetRange(split[0], split[1]);
            } else {
                //default subnetwork: 192.168.10.0
                //single ip: 192.168.0.10
                if (rawRange.endsWith(".0")) {
                    ranges[i] = new SubnetRange(rawRange);
                } else {
                    ranges[i] = new SingleIp(rawRange);
                }
            }
        }
        return ranges;
    }
}
