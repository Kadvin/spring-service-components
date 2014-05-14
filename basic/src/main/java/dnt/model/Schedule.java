/**
 * @author XiongJie, Date: 13-10-28
 */
package dnt.model;

/**
 * <h1>时间计划</h1>
 */
public interface Schedule {
    String getStartOn();

    String getStopOn();

    String getCron();

    long getStartTimestamp();

    long getStopTimestamp();

    void stop();

    void validate();
}
