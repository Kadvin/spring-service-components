/**
 * Developer: Kadvin Date: 14-7-16 上午10:47
 */
package net.happyonroad.platform.support;

import net.happyonroad.platform.services.ServicePackageEvent;
import net.happyonroad.spring.Bean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

/**
 * Forward service package event from platform context to spring mvc context
 */
public class ServicePackageEventForwarder extends Bean implements ApplicationListener<ServicePackageEvent> {
    ApplicationContext target;

    @Override
    public void onApplicationEvent(ServicePackageEvent event) {
        if( target == null ) return;
        //防止死循环导致的StackOverFlow
        // Platform Context --forwarder--> Spring Mvc Context
        // Spring Mvc Context -->multicast--> Parent Context(Platform Context)
        // ...
        if( event.forwarded() == target ) return;
        event.forwardingTo(target);
        target.publishEvent(event);
    }

    public void bind(ApplicationContext target){
        this.target = target;
    }
}
