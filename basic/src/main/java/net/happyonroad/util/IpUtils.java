/**
 * Developer: Kadvin Date: 14-5-19 下午2:19
 */
package net.happyonroad.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * IP地址工具
 */
public final class IpUtils {

    public static Set<String> getLocalAddresses(){
        Set<String> localAddresses = new HashSet<String>(2); ;
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                Enumeration<InetAddress> addresses = nic.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    String hostAddress = address.getHostAddress();
                    if(hostAddress.contains(":")) continue; // ipv6
                    if("127.0.0.1".equals(hostAddress)) continue;
                    localAddresses.add(hostAddress);
                }
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Can't find local address", e);
        }
        return localAddresses;
    }

    /**
     * <h3>规范化MAC地址</h3>
     * MAC地址有多种表达形式，可能以空格，减号，冒号分割，规范化之后，都以冒号分割
     *
     * @param macAddress mac地址
     * @return 规范化之后的mac地址
     */
    public static String regularMAC(String macAddress) {
        return macAddress.replaceAll("[\\s:-]", ":").toLowerCase();
    }
}
