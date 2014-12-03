/**
 * Developer: Kadvin Date: 14-5-16 上午9:33
 */
package net.happyonroad.spring;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * The bean support publish event
 */
public class EventSupportBean extends Bean implements ApplicationEventPublisherAware{
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    protected void publish(ApplicationEvent event){
        publisher.publishEvent(event);
    }
}
