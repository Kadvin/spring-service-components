/**
 * Developer: Kadvin Date: 14-5-19 下午2:19
 */
package net.happyonroad.util;

import java.util.regex.Pattern;

/**
 * IP地址工具
 */
public final class IpUtils {
    static Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

    public static boolean isIpv4(String dnsNameOrIp) {
        return IP_PATTERN.matcher(dnsNameOrIp).matches();
    }
}
