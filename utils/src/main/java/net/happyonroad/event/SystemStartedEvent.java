/**
 * Developer: Kadvin Date: 15/1/23 下午2:21
 */
package net.happyonroad.event;

import net.happyonroad.component.core.ComponentContext;

/**
 * All service packages are loaded and ready for further works
 */
public class SystemStartedEvent extends SystemEvent {
    public SystemStartedEvent(ComponentContext source) {
        super(source);
    }
}
