/**
 * @author XiongJie, Date: 14-1-13
 */
package net.happyonroad.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/** A single ip range*/
public class SingleIp extends IpRange{

    private final String ip;

    @JsonCreator
    public SingleIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean include(String ip) {
        return this.ip.equals(ip);
    }

    public String getAddress() {
        return ip;
    }

    @Override
    public String asParam() {
        return regular(getAddress());
    }

    @Override
    public String toJson() {
        return ip;
    }

    public String toString(){
        return ip;
    }
}
