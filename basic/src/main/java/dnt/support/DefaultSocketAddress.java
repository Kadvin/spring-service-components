/**
 * @author XiongJie, Date: 14-1-9
 */
package dnt.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dnt.model.SocketAddress;

import java.net.InetSocketAddress;

/** The wrapper of InetSocketAddress to support object be output as json */
public class DefaultSocketAddress extends JsonSupport implements SocketAddress {
    private static final long serialVersionUID = -2122351650922095856L;

    private String host;
    private int port;

    public DefaultSocketAddress(InetSocketAddress address) {
        this(address.getAddress().getHostAddress(), address.getPort());
    }

    @JsonCreator
    public DefaultSocketAddress(@JsonProperty("host") String host,
                                @JsonProperty("port") int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost(){
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultSocketAddress)) return false;

        DefaultSocketAddress that = (DefaultSocketAddress) o;

        if (port != that.port) return false;
        if (!host.equals(that.host)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return getHost() + ":" + getPort();
    }
}
