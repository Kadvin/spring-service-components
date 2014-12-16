/**
 * Developer: Kadvin Date: 14-7-16 上午10:47
 */
package net.happyonroad.platform.support;

import net.happyonroad.spring.Bean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Forward application event from platform context to spring mvc context
 */
public class PlatformEventForwarder extends Bean
        implements ApplicationListener<ApplicationEvent> {
    ApplicationContext springMvcContext;
    private Set<ApplicationEvent> events = new HashSet<ApplicationEvent>();

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (springMvcContext == null) return;
        //防止死循环导致的StackOverFlow
        // Platform Context --forwarder--> Spring Mvc Context
        // Spring Mvc Context -->multicast--> Parent Context(Platform Context)
        // ...
        if (remembered(event)) return;
        remember(event);
        springMvcContext.publishEvent(event);

    }

    private boolean remembered(ApplicationEvent event) {
        return events.contains(event);
    }

    private void remember(ApplicationEvent event) {
        events.add(event);
    }

    public void bind(ApplicationContext target){
        this.springMvcContext = target;
    }
}
