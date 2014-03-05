/**
 * @author XiongJie, Date: 14-1-13
 */
package dnt.model;

/**
 * The ip range
 */
public class StartAndEndRange extends IpRange {
    /* the start ip, can be null*/
    private String startIp;
    /* the end ip, can be null*/
    private String endIp;

    public StartAndEndRange(String startIp, String endIp) {
        this.startIp = startIp;
        this.endIp = endIp;
        if(this.startIp == null && this.endIp == null)
            throw new IllegalArgumentException("You must specify a start or end ip for the range");
    }

    @Override
    public boolean include(String ip) {
        //TODO, it shouldn't depends on string compare, we should split ip in dotted
        if( this.startIp != null ){
            if(ip.compareTo(startIp) < 0 )return  false;
        }
        if( this.endIp != null ){
            if(ip.compareTo(endIp) > 0 )return  false;
        }
        return true;
    }
}
