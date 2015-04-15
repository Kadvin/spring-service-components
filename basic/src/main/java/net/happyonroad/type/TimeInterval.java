/**
 * @author XiongJie, Date: 13-10-24
 */
package net.happyonroad.type;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 时间字符串格式化/解析工具 */
public class TimeInterval implements Serializable{
    private static final long serialVersionUID = -2549444675116336461L;
    static Pattern pattern = Pattern.compile(
            "(?:(\\d+)(y))?(?:(\\d+)(M))?(?:(\\d+)(d))?(?:(\\d+)(h))?(?:(\\d+)(m))?(?:(\\d+)(s))?(?:(\\d+)(ms))?");
    static Pattern digital = Pattern.compile("(\\d+)");

    private String interval;
    private long   milliseconds;

    @SuppressWarnings("UnusedDeclaration") //for serialization
    public TimeInterval() {
        this("1s");
    }

    public TimeInterval(String interval) {
        this.interval = interval;
        this.milliseconds = parseLong(interval);
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return interval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeInterval)) return false;

        TimeInterval that = (TimeInterval) o;

        if (milliseconds != that.milliseconds) return false;
        if (!interval.equals(that.interval)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = interval.hashCode();
        result = 31 * result + (int) (milliseconds ^ (milliseconds >>> 32));
        return result;
    }

    /**
     * Parse the value into human readable string
     * @param value the value
     * @param unit the string unit
     * @return the string
     */
    public static String parse(long value, String unit){
        return String.format("%.2f%s", (float)value / unitValue(unit), unit);
    }

    /**
     * 把毫秒级数值转换为可阅读的字符串，如
     *  1y3M4d5h6m7s100ms
     *
     * @param milliseconds 毫秒
     * @return 结果字符串
     */
    public static String parse(long milliseconds){
        if( milliseconds == 0 ) return "0ms";
        long year, month, day, hour, minute, second, ms;
        year = milliseconds / unitValue("y");
        long left = milliseconds % unitValue("y");
        month = left / unitValue("M");
        left = milliseconds % unitValue("M");
        day = left / unitValue("d");
        left = milliseconds % unitValue("d");
        hour = left / unitValue("h");
        left = milliseconds % unitValue("h");
        minute = left / unitValue("m");
        left = milliseconds % unitValue("m");
        second = left / unitValue("s");
        ms = milliseconds % unitValue("s");
        StringBuilder sb = new StringBuilder();
        if(year > 0 )sb.append(year).append("y");
        if(month > 0 )sb.append(month).append("M");
        if(day > 0 )sb.append(day).append("d");
        if(hour > 0 )sb.append(hour).append("h");
        if(minute > 0 )sb.append(minute).append("m");
        if(second > 0 )sb.append(second).append("s");
        if(ms > 0 )sb.append(ms).append("ms");
        return sb.toString();
    }

    public static int parseInt(String interval){
        return (int) parseLong(interval);
    }

    public static long parseLong(String interval){
        Matcher matcher = pattern.matcher(interval);
        long milliseconds = 0;
        if (matcher.matches()) {
            String year = matcher.group(1);
            String month = matcher.group(3);
            String day = matcher.group(5);
            String hour = matcher.group(7);
            String minute = matcher.group(9);
            String second = matcher.group(11);
            String millisecond = matcher.group(13);

            if( year != null) milliseconds += Integer.valueOf(year) * unitValue("y");
            if( month != null) milliseconds += Integer.valueOf(month) * unitValue("M");
            if( day != null) milliseconds += Integer.valueOf(day) * unitValue("d");
            if( hour != null) milliseconds += Integer.valueOf(hour) * unitValue("h");
            if( minute != null) milliseconds += Integer.valueOf(minute) * unitValue("m");
            if( second != null) milliseconds += Integer.valueOf(second) * unitValue("s");
            if( millisecond != null) milliseconds += Integer.valueOf(millisecond) * unitValue("ms");
        }else if(digital.matcher(interval).matches()){
            milliseconds = Long.valueOf(interval);
        }else{
            throw new IllegalArgumentException(String.format("Wrong time interval format: %s, " +
                                                             "please ensure the unit sequence: y, M, d, h, m, s, ms", interval));
        }
        return milliseconds;
    }

    private static long unitValue(String unit) {
        if ("ms".equalsIgnoreCase(unit)) {
            return 1;
        } else if ("s".equalsIgnoreCase(unit)) {
            return 1000;
        } else if ("m".equals(unit)) {
            return 1000 * 60;
        } else if ("h".equalsIgnoreCase(unit)) {
            return 1000 * 60 * 60;
        } else if ("d".equalsIgnoreCase(unit)) {
            return 1000 * 60 * 60 * 24;
        } else if ("w".equalsIgnoreCase(unit)) {
            return 1000 * 60 * 60 * 24 * 7;
        } else if ("M".equals(unit)) {
            return 1000 * 60 * 60 * 24 * 30L;
        } else if ("y".equalsIgnoreCase(unit)) {
            return 1000 * 60 * 60 * 24 * 365L;
        } else {
            throw new IllegalArgumentException(String.format("Unrecognized unit: %s", unit));
        }
    }
}
