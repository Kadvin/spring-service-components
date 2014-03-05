/**
 * @author XiongJie, Date: 14-1-14
 */
package dnt.model;

import net.minidev.json.JSONAware;

import java.net.InetAddress;

/**
 * The wrapper of InetAddress, can't extends InetAddress, just wrap it
 */
public class InetAddressWrapper implements JSONAware, HostAddress {
    private static final long serialVersionUID = -9124503181449974515L;
    private final InetAddress address;

    public InetAddressWrapper(InetAddress address) {
        this.address = address;
    }

    @Override
    public String getHost() {
        return address.getHostAddress();
    }

    @Override
    public String toJSONString() {
        return getHost();
    }

    @Override
    public String toString() {
        return getHost();
    }
}
