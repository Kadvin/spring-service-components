/**
 * Developer: Kadvin Date: 15/1/23 下午2:22
 */
package net.happyonroad.event;

import net.happyonroad.component.core.ComponentContext;

/**
 * All service packages are unloading
 */
public class SystemStoppingEvent extends SystemEvent {
    public SystemStoppingEvent(ComponentContext source) {
        super(source);
    }
}
