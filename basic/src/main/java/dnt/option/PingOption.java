/**
 * Developer: Kadvin Date: 14-5-20 下午2:29
 */
package dnt.option;

import dnt.model.Option;

/**
 * Ping的选项，这个选项要底层支持(现在底层采用的可能是 nmap)
 */
public class PingOption implements Option{
    private int packageCount = 1;
    private int delay = 5 ;// ms
    private int packageSize = 32;//Bytes
    private int timeout = 2;//seconds

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(int packageSize) {
        this.packageSize = packageSize;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
