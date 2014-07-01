/**
 * Developer: Kadvin Date: 14-6-13 下午2:11
 */
package dnt.util;

import com.fasterxml.jackson.databind.util.StdConverter;
import dnt.model.Address;
import dnt.support.DefaultHostAddress;
import dnt.support.DefaultNetworkAddress;
import dnt.support.DefaultSocketAddress;

import java.util.Map;

/**
 * MO Address Converter
 */
public class AddressConverter extends StdConverter<Map<String, Object>, Address> {
    @Override
    public Address convert(Map<String, Object> value) {
        String host = value.get("host").toString();
        if( value.containsKey("port") ){
            Integer port = Integer.valueOf(value.get("port").toString());
            return new DefaultSocketAddress(host, port);
        }else if (value.containsKey("mask") ){
            String address = (String) value.get("address");
            String mask = (String) value.get("mask");
            return new DefaultNetworkAddress(address, mask);
        }else {
            return new DefaultHostAddress(host);
        }
    }

    // for raw string to address
    public Address convert(String value) {
        if( value.contains(":") ){
            String[] hostAndPort = value.split(":");
            String host = hostAndPort[0];
            int port = Integer.valueOf(hostAndPort[1]);
            return new DefaultSocketAddress(host, port);
        }else if (value.contains("/") ){
            String[] addressAndMask = value.split("/");
            String address = addressAndMask[0];
            String mask = addressAndMask[1];
            return new DefaultNetworkAddress(address, mask);
        }else{
            return new DefaultHostAddress(value);
        }
    }
}
