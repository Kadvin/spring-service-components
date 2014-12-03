/**
 * Developer: Kadvin Date: 14-5-20 下午2:54
 */
package net.happyonroad.option;

import net.happyonroad.model.Option;
import net.happyonroad.model.Schedule;

/**
 * 自动发现的选项，这与发现的选项并不一样，这侧重于调度
 */
public class AutoDiscoveryOption implements Option {
    private Schedule schedule;

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
