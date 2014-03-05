/**
 * Developer: Kadvin Date: 14-2-19 下午12:56
 */
package dnt.concurrent;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.util.Date;

/**
 * Only once trigger
 */
public class OnceTrigger implements Trigger {
    private long at;

    public OnceTrigger(long at) {
        this.at = at;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        if(System.currentTimeMillis() < at) return new Date(at);
        return null;
    }
}
