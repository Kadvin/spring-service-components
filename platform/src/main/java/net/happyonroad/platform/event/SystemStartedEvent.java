/**
 * Developer: Kadvin Date: 15/1/23 下午2:21
 */
package net.happyonroad.platform.event;

/**
 * All service packages are loaded and ready for further works
 */
public class SystemStartedEvent extends SystemEvent {
    public SystemStartedEvent(Object source) {
        super(source);
    }
}
