/**
 * @author XiongJie, Date: 14-1-9
 */
package dnt.model;

import net.minidev.json.JSONAware;

import java.net.InetSocketAddress;

/** The wrapper of InetSocketAddress to support object be output as json */
public class InetSocketAddressWrapper extends InetSocketAddress implements JSONAware, SocketAddress {
    private static final long serialVersionUID = -2122351650922095856L;

    public InetSocketAddressWrapper(InetSocketAddress address) {
        super(address.getAddress(), address.getPort());
    }

    public InetSocketAddressWrapper(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public String toJSONString() {
        return "{\"host\":\"" + getHost() + "\",\"port\":" + getPort() + "}";
    }

    @SuppressWarnings("UnusedDeclaration for reflection")
    public String getHost(){
        //TODO ensure return host address
        return getHostName();
    }

    @Override
    public String toString() {
        return getHost() + ":" + getPort();
    }
}
