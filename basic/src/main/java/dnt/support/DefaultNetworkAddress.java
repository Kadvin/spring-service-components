/**
 * Developer: Kadvin Date: 14-6-16 下午5:51
 */
package dnt.support;

import dnt.model.Address;

/**
 * Description here
 */
public class DefaultNetworkAddress extends JsonSupport implements Address {
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
