/**
 * Developer: Kadvin Date: 14-6-16 下午5:51
 */
package net.happyonroad.support;

import net.happyonroad.model.Address;

/**
 * The default network address
 */
public class DefaultNetworkAddress extends JsonSupport implements Address {
    private static final long serialVersionUID = -6139003363957900976L;
    private String address;
    private String mask;

    public DefaultNetworkAddress() {
    }

    public DefaultNetworkAddress(String address, String mask) {
        this.address = address;
        this.mask = mask;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }
}
