/**
 * Developer: Kadvin Date: 14-4-21 下午7:16
 */
package dnt.util;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.List;

/**
 * The rmi registry locator
 */
public class RmiRegistryLocator {

    public static Registry locateRegistry(int port) {
        return locateRegistry(null, port);
    }

    public static Registry locateRegistry(String host, int port) {
        try {
            if (StringUtils.isEmpty(host) || isLocal(host)) {
                try {
                    return LocateRegistry.createRegistry(port);
                } catch (RemoteException e) {
                    return LocateRegistry.getRegistry(port);
                }
            } else {
                return LocateRegistry.getRegistry(host, port);
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Can't get rmi registry for " + host + ":" + port);
        }
    }

    private static boolean isLocal(String host) throws Exception {
        InetAddress address = InetAddress.getByName(host);
        Enumeration<NetworkInterface> itfs = NetworkInterface.getNetworkInterfaces();
        while (itfs.hasMoreElements()) {
            NetworkInterface itf = itfs.nextElement();
            List<InterfaceAddress> addresses = itf.getInterfaceAddresses();
            for (InterfaceAddress challenger : addresses) {
                if (address.equals(challenger.getAddress())) return true;
            }
        }
        return false;
    }
}
