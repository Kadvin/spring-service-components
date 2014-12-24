/**
 * Developer: Kadvin Date: 14/12/22 下午7:55
 */
package net.happyonroad.type;

/**
 * 监控计划
 */
public class Schedule extends TimeSpan {
    //监控频度
    private TimeInterval frequency;
    //相对于频率期间的起始偏移量，单位为毫秒，用于削峰填谷
    private long offset;

    public TimeInterval getFrequency() {
        return frequency;
    }

    public void setFrequency(TimeInterval frequency) {
        this.frequency = frequency;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
