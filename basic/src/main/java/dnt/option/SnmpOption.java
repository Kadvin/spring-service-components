/**
 * Developer: Kadvin Date: 14-5-6 下午8:47
 */
package dnt.option;

import dnt.model.Option;

/**
 * SNMP Option
 */
public class SnmpOption implements Option {
    private int delay = 5;// milliseconds between two SNMP request target to one agent
    private boolean retry = false;//when failed, retry or not
    private boolean ignoreOverflow = true;
    private boolean ignoreZeroValues = true;
    private boolean limitTo32BitCounter = false;//try 64 bit if possible
    private boolean bulkGet = true;// use bulk get instead of get

    //端口映射的规则
    // port name template
    // port name updates
    // port identification(automatic, ifAlias, ifDesc, ifName
    // start port
    // end port


    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public boolean isIgnoreOverflow() {
        return ignoreOverflow;
    }

    public void setIgnoreOverflow(boolean ignoreOverflow) {
        this.ignoreOverflow = ignoreOverflow;
    }

    public boolean isIgnoreZeroValues() {
        return ignoreZeroValues;
    }

    public void setIgnoreZeroValues(boolean ignoreZeroValues) {
        this.ignoreZeroValues = ignoreZeroValues;
    }

    public boolean isLimitTo32BitCounter() {
        return limitTo32BitCounter;
    }

    public void setLimitTo32BitCounter(boolean limitTo32BitCounter) {
        this.limitTo32BitCounter = limitTo32BitCounter;
    }

    public boolean isBulkGet() {
        return bulkGet;
    }

    public void setBulkGet(boolean bulkGet) {
        this.bulkGet = bulkGet;
    }
}
