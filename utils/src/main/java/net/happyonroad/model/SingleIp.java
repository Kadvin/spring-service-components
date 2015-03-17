/**
 * @author XiongJie, Date: 14-1-13
 */
package net.happyonroad.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A single IP range*/
public class SingleIp extends IpRange{

    private static final long serialVersionUID = 6746242273258267020L;
    private final String address;

    @JsonCreator
    public SingleIp(@JsonProperty("address") String ip) {
        this.address = ip;
    }

    @Override
    public boolean include(String ip) {
        return this.address.equals(ip);
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String asParam() {
        return regular(getAddress());
    }

    @Override
    public String toJson() {
        return address;
    }

    public String toString(){
        return address;
    }
}
