/**
 * Developer: Kadvin Date: 14/12/22 下午7:55
 */
package net.happyonroad.type;

/**
 * 监控计划
 */
public class Schedule extends TimeSpan {
    private static final long serialVersionUID = 4754218267494981545L;

    private String cron;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    @Override
    public String toString() {
        return "Schedule(" + format(getStartAt(), "ever") + " -> " + format(getEndAt(), "forever") + ":" + cron + ")";
    }

}
