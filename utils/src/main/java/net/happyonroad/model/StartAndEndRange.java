/**
 * @author XiongJie, Date: 14-1-13
 */
package net.happyonroad.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The ip range
 */
public class StartAndEndRange extends IpRange {
    private static final long serialVersionUID = 1116632019398537876L;
    /* the start ip, can be null*/
    private String startIp;
    /* the end ip, can be null*/
    private String endIp;

    @JsonCreator
    public StartAndEndRange(@JsonProperty("start") String startIp,
                            @JsonProperty("end") String endIp) {
        this.startIp = startIp;
        this.endIp = endIp;
        if (this.startIp == null && this.endIp == null)
            throw new IllegalArgumentException("You must specify a start or end ip for the range");
    }

    @Override
    public boolean include(String ip) {
        //TODO, it shouldn't depends on string compare, we should split ip in dotted
        if (this.startIp != null) {
            if (ip.compareTo(startIp) < 0) return false;
        }
        if (this.endIp != null) {
            if (ip.compareTo(endIp) > 0) return false;
        }
        return true;
    }

    public String getStart() {
        return startIp;
    }

    public String getEnd() {
        return endIp;
    }

    @Override
    public String asParam() {
        return regular(getStart() + "-" + getEnd());
    }

    public String toString(){
        return getStart() + "-" + getEnd();
    }
}
