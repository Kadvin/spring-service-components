/**
 * Developer: Kadvin Date: 14-5-16 上午9:36
 */
package dnt.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.jmx.export.MBeanExportOperations;

import javax.management.ObjectName;

/**
 * The bean support application
 */
public class ApplicationSupportBean extends TranslateSupportBean implements ApplicationContextAware{
    protected ApplicationContext applicationContext;

    protected MBeanExportOperations mbeanExporter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        try{
            mbeanExporter = applicationContext.getBean(MBeanExportOperations.class);
        }catch (NoSuchBeanDefinitionException ex){
            mbeanExporter = null;
        }
    }

    /**
     * Publish event to the application context and all parents
     *
     * @param event the event to be published
     */
    protected void publish(ApplicationEvent event) {
        this.applicationContext.publishEvent(event);
    }

    /**
     * Register a mBean with specified object name
     *
     * @param bean the bean to be registered
     * @param name the object name of the bean
     */
    protected void registerMbean(Object bean, ObjectName name) {
        //TODO 在系统刚刚启动时构建的对象没有被export
        if( mbeanExporter != null )
            mbeanExporter.registerManagedResource(bean, name);
    }


}
