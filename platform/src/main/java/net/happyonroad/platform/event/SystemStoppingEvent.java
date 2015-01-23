/**
 * Developer: Kadvin Date: 15/1/23 下午2:22
 */
package net.happyonroad.platform.event;

/**
 * All service packages are unloaded
 */
public class SystemStoppingEvent extends SystemEvent {
    public SystemStoppingEvent(Object source) {
        super(source);
    }
}
