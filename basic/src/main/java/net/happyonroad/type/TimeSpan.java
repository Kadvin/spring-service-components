/**
 * Developer: Kadvin Date: 14-5-6 下午10:17
 */
package net.happyonroad.type;

import java.util.Date;

/**
 * The time span
 */
public class TimeSpan {
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
}
