/**
 * Developer: Kadvin Date: 14-5-16 上午9:46
 */
package net.happyonroad.platform.services;

import org.springframework.context.ApplicationEvent;

/**
 * 所有服务包的事件
 */
public class ServicePackagesEvent extends ApplicationEvent {
    public ServicePackagesEvent(Object source) {
        super(source);
    }

    /**
     * All service packages are loaded and ready for further works
     */
    public static class LoadedEvent extends ServicePackagesEvent {
        public LoadedEvent(Object source) {
            super(source);
        }
    }

    /**
     * All service packages are unloaded
     */
    public static class UnloadedEvent extends ServicePackagesEvent {
        public UnloadedEvent(Object source) {
            super(source);
        }
    }
}
