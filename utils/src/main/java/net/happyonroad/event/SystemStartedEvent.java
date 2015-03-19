/**
 * Developer: Kadvin Date: 15/1/23 下午2:21
 */
package net.happyonroad.event;

import net.happyonroad.component.core.ComponentContext;

/**
 * All service packages are loaded and ready for further works
 */
public class SystemStartedEvent extends SystemEvent {
    private static final long serialVersionUID = -1957649504004275588L;

    public SystemStartedEvent(ComponentContext source) {
        super(source);
    }
}
