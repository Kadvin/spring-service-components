/**
 * @author XiongJie, Date: 13-10-28
 */
package net.happyonroad.model;

/**
 * <h1>时间计划</h1>
 *
 * 用于表达：
 * 在某段时间之内，以cron方式定期执行的任务
 */
public interface CronSchedule {
    String getStartOn();

    String getStopOn();

    String getCron();

    long getStartTimestamp();

    long getStopTimestamp();

    void stop();

    void validate();
}
