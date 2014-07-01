/**
 * Developer: Kadvin Date: 14-5-20 下午2:42
 */
package dnt.option;

import dnt.model.Option;
import dnt.model.Schedule;
//import dnt.type.TimeInterval;
import dnt.type.TimeSpan;

/**
 * 监控选项
 */
public class MonitorOption implements Option{
    // 默认监控频率
    // 这个与 schedule 重复了
    //private TimeInterval scanInterval;
    // 默认监控计划（仅在此时间窗内进行监控)
    private Schedule schedule;//monitoring
    // 默认维护时间窗口（在此时间窗内监控，但不发出告警)
    private TimeSpan maintenanceWindow;//not monitoring

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public TimeSpan getMaintenanceWindow() {
        return maintenanceWindow;
    }

    public void setMaintenanceWindow(TimeSpan maintenanceWindow) {
        this.maintenanceWindow = maintenanceWindow;
    }
}
