/**
 * @author XiongJie, Date: 14-1-13
 */
package net.happyonroad.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.net.util.SubnetUtils;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * The subnetwork range
 */
public class SubnetRange extends IpRange{

    private static final long serialVersionUID = -50738278123230056L;
    private transient SubnetUtils.SubnetInfo info;
    private String address, mask;

    /**
     * 构建一个子网对象
     *
     * @param subnet 子网ip
     * @param mask   掩码
     */
    @JsonCreator
    public SubnetRange(@JsonProperty("address") String subnet,
                       @JsonProperty("mask") String mask) {
        this.info = new SubnetUtils(subnet, mask).getInfo();
        this.address = this.info.getNetworkAddress();
        this.mask = this.info.getNetmask();
    }

    public SubnetRange(String range) {
        this.info = new SubnetUtils(range).getInfo();
        this.address = this.info.getNetworkAddress();
        this.mask = this.info.getNetmask();
    }

    @Override
    public boolean include(String ip) {
        return info.isInRange(ip);
    }

    public String getAddress(){
        return address;
    }

    public String getMask(){
        return mask;
    }

    @Override
    public String asParam() {
        return regular(address);
    }

    public String toString(){
        return info.getCidrSignature();
    }

    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException
    {
        s.defaultReadObject();
        this.info = new SubnetUtils(this.address, mask).getInfo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubnetRange)) return false;

        SubnetRange that = (SubnetRange) o;

        if (!address.equals(that.address)) return false;
        if (!mask.equals(that.mask)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + mask.hashCode();
        return result;
    }
}
