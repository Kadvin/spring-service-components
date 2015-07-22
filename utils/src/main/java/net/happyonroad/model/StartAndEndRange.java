/**
 * @author XiongJie, Date: 14-1-13
 */
package net.happyonroad.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The ip range with start/end segments
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

    /**
     * <h2>判断ip是否在本范围内</h2>
     * 现在的实现，并不是用 start <= ip <= end 来判断
     * 而是将ip拆为四段，每段单独判断
     *
     * @param ip 被判断ip
     * @return 是否在本范围内
     */
    @Override
    public boolean include(String ip) {
        String[] starts = getStart().split("\\.");
        String[] ends = getEnd().split("\\.");
        String[] challenges = ip.split("\\.");
        for (int i = 0; i < challenges.length; i++) {
            int challenge = Integer.valueOf(challenges[i]);
            int start = Integer.valueOf(starts[i]);
            int end = Integer.valueOf(ends[i]);
            if( challenge < start) return false;
            if( challenge > end ) return false;
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
        return regular(toString());
    }

    public String toString() {
        String[] starts = getStart().split("\\.");
        String[] ends = getEnd().split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < starts.length; i++) {
            String start = starts[i];
            String end = ends[i];
            if (start.equals(end)) {
                sb.append(start);
            } else {
                sb.append(start).append("-").append(end);
            }
            if (i < 3) sb.append(".");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StartAndEndRange)) return false;

        StartAndEndRange that = (StartAndEndRange) o;

        if (!endIp.equals(that.endIp)) return false;
        if (!startIp.equals(that.startIp)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startIp.hashCode();
        result = 31 * result + endIp.hashCode();
        return result;
    }
}
