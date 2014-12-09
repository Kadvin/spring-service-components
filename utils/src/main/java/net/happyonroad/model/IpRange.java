/**
 * @author XiongJie, Date: 14-1-13
 */

package net.happyonroad.model;

import net.happyonroad.support.JsonSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * Judge a ip in the range or not
 */
public abstract class IpRange extends JsonSupport implements PathParameter {
    public abstract boolean include(String ip);

    @SuppressWarnings("unchecked")
    public static <T> T parseJson(String content, Class<T> theClass) {
        if( theClass == SubnetRange.class){
            // {"address" : "192.168.21.0", "mask" : "255.255.255.0"}
            Map<String, String> map = JsonSupport.parseJson(content, HashMap.class);
            return (T)new SubnetRange(map.get("address"), map.get("mask"));
        }else if (theClass == SingleIp.class ){

            // "192.168.21.10"
            return (T) new SingleIp(content);
        }else if( theClass == StartAndEndRange.class ){
            // {"start" : "192.168.21.10", "end" : "192.168.21.200"}
            Map<String, String> map = JsonSupport.parseJson(content, HashMap.class);
            return (T) new StartAndEndRange(map.get("start"), map.get("end"));
        }else if( IpRange.class.isAssignableFrom(theClass)){
            throw new UnsupportedOperationException("Can't parse json for ip range class:" + theClass);
        }else {
            throw new UnsupportedOperationException("Can't parse json for common class:" + theClass);
        }
    }

    public static IpRange[] parse(String ips) {
        String[] rawRanges = ips.split(",\\s*");
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
