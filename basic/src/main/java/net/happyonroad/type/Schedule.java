/**
 * Developer: Kadvin Date: 14/12/22 下午7:55
 */
package net.happyonroad.type;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * 监控计划
 */
public class Schedule implements Serializable {
    private static final long serialVersionUID = 4754218267494981545L;

    // 调度类型，分为: Cron, Intervals, Matrix 三种
    private ScheduleType type = ScheduleType.Cron;

    // CRON表达式
    private String   expression;
    // 时间段
    private String[] intervals;
    // 时间表
    private String[] matrix;

    public ScheduleType getType() {
        return type;
    }

    public void setType(ScheduleType type) {
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String[] getIntervals() {
        return intervals;
    }

    public void setIntervals(String[] intervals) {
        this.intervals = intervals;
    }

    public String[] getMatrix() {
        return matrix;
    }

    public void setMatrix(String[] matrix) {
        this.matrix = matrix;
    }

    @Override
    public String toString() {
        return "Schedule(" + type + "=" + value() + ")";
    }

    public String value() {
        switch (type) {
            case Cron:
                return expression;
            case Matrix:
                return StringUtils.join(matrix, ",");
            default:
                return StringUtils.join(intervals, ",");
        }
    }

}
