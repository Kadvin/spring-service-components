/**
 * Developer: Kadvin Date: 14-5-16 上午9:36
 */
package net.happyonroad.spring;

import net.happyonroad.component.core.ComponentContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.jmx.export.MBeanExportOperations;

import javax.management.ObjectName;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * The bean support application
 */
public class ApplicationSupportBean extends TranslateSupportBean
        implements ApplicationContextAware, ApplicationEventPublisher {
    protected static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    protected static Validator validator = factory.getValidator();

    @Autowired
    private ComponentContext componentContext;
    protected ApplicationContext applicationContext;

    private MBeanExportOperations mbeanExporter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        super.setMessageSource(applicationContext);
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
    public void publishEvent(ApplicationEvent event) {
        //向所有的context发布，context里面有防止重复的机制
        for (ApplicationContext context : componentContext.getApplicationFeatures()) {
            if( context != null ) {
                if( context instanceof ConfigurableApplicationContext){
                    if( !((ConfigurableApplicationContext) context).isActive() ) continue;
                }
                context.publishEvent(event);
            }
        }
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
