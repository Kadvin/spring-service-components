/**
 * Developer: Kadvin Date: 14-5-16 上午9:36
 */
package dnt.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

/**
 * The bean support application
 */
public class ApplicationSupportBean extends TranslateSupportBean implements ApplicationContextAware{
    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected void publish(ApplicationEvent event){
        this.applicationContext.publishEvent(event);
    }
}
