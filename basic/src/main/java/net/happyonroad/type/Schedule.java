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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        if (!super.equals(o)) return false;

        Schedule schedule = (Schedule) o;

        if (offset != schedule.offset) return false;
        if (!frequency.equals(schedule.frequency)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + frequency.hashCode();
        result = 31 * result + (int) (offset ^ (offset >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Schedule(" + frequency + formatOffset() + ")";
    }

    private String formatOffset(){
        if( offset == 0 ) return "";
        else return "+" + offset + "ms";
    }
}
