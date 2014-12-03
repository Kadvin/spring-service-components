/**
 * @author XiongJie, Date: 14-1-14
 */
package net.happyonroad.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.model.HostAddress;

import java.net.InetAddress;

/**
 * The wrapper of InetAddress, can't extends InetAddress, just wrap it
 */
public class DefaultHostAddress extends JsonSupport implements HostAddress {
    private static final long serialVersionUID = -9124503181449974515L;
    private final String host;

    public DefaultHostAddress(InetAddress host) {
        this.host = host.getHostAddress();
    }

    @JsonCreator
    public DefaultHostAddress(@JsonProperty("host") String host) {
        this.host = host;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String toString() {
        return getHost();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultHostAddress)) return false;

        DefaultHostAddress that = (DefaultHostAddress) o;

        if (!host.equals(that.host)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return host.hashCode();
    }
}
