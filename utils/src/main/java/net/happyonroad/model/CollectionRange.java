package net.happyonroad.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * <h1>若干个IP地址集合构成的范围</h1>
 *
 * @author Jay Xiong
 */
public class CollectionRange extends IpRange{
    private static final long serialVersionUID = 5763420627201014441L;

    private final Set<String> addresses;

    @JsonCreator
    public CollectionRange(@JsonProperty("addresses") Set<String> addresses) {
        this.addresses = addresses;
    }

    @Override
    public boolean include(String ip) {
        return addresses.contains(ip);
    }

    @Override
    public String asParam() {
        Set<String> params = new HashSet<String>(addresses.size());
        for (String address : addresses) {
            params.add(regular(address));
        }
        return StringUtils.join(params,",");
    }

    @Override
    public String toString() {
        //为了能被nmap解析，必须用分号
        return StringUtils.join(addresses, " ");
    }

    public Set<String> getAddresses() {
        return addresses;
    }
}
