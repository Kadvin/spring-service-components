/**
 * Developer: Kadvin Date: 14-5-6 下午10:17
 */
package net.happyonroad.type;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The time span: 这个对象比joda的interval小得多（json之后)
 */
public class TimeSpan implements Serializable{

    private static final long serialVersionUID = 640946156242165862L;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Date startAt;
    private Date endAt;

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    @Override
    public String toString() {
        return "TimeSpan(" + format(startAt, "ever") + " -> " + format(endAt, "forever") + ")";
    }

    String format(Date date, String defaults) {
        return date == null ? defaults : sdf.format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSpan)) return false;

        TimeSpan timeSpan = (TimeSpan) o;

        if (endAt != null ? !endAt.equals(timeSpan.endAt) : timeSpan.endAt != null) return false;
        if (startAt != null ? !startAt.equals(timeSpan.startAt) : timeSpan.startAt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startAt != null ? startAt.hashCode() : 0;
        result = 31 * result + (endAt != null ? endAt.hashCode() : 0);
        return result;
    }
}
