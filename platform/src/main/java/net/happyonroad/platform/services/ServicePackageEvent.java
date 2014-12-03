/**
 * Developer: Kadvin Date: 14-5-16 上午9:46
 */
package net.happyonroad.platform.services;

import net.happyonroad.component.core.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

/**
 * 所有服务包的事件
 */
public class ServicePackageEvent extends ApplicationEvent {
    private ApplicationContext forward;

    public ServicePackageEvent(Component source) {
        super(source);
    }

    @Override
    public Component getSource() {
        return (Component) super.getSource();
    }

    public ApplicationContext forwarded() {
        return forward;
    }

    public void forwardingTo(ApplicationContext target) {
        this.forward = target;
    }

    /**
     * the service packages is loading
     */
    public static class LoadingEvent extends ServicePackageEvent {
        public LoadingEvent(Component source) {
            super(source);
        }
    }


    /**
     * the service packages are loaded and ready for further works
     */
    public static class LoadedEvent extends ServicePackageEvent {
        public LoadedEvent(Component source) {
            super(source);
        }
    }

    /**
     * the service packages is unloading
     */
    public static class UnloadingEvent extends ServicePackageEvent {
        public UnloadingEvent(Component source) {
            super(source);
        }
    }

    /**
     * the service packages is unloaded
     */
    public static class UnloadedEvent extends ServicePackageEvent {
        public UnloadedEvent(Component source) {
            super(source);
        }
    }
}
