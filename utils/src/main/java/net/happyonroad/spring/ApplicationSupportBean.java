/**
 * Developer: Kadvin Date: 14-5-16 上午9:36
 */
package net.happyonroad.spring;

import net.happyonroad.component.core.ComponentContext;
import net.happyonroad.spring.context.ContextUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jmx.export.MBeanExportOperations;

import javax.management.ObjectName;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The bean support application
 */
public class ApplicationSupportBean extends TranslateSupportBean
        implements ApplicationContextAware, ApplicationEventPublisher {

    protected static ValidatorFactory factory     = Validation.buildDefaultValidatorFactory();
    protected static Validator        validator   = factory.getValidator();

    @Autowired
    private   ComponentContext   componentContext;
    protected ApplicationContext applicationContext;

    private MBeanExportOperations mbeanExporter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        super.setMessageSource(applicationContext);
        try {
            mbeanExporter = applicationContext.getBean(MBeanExportOperations.class);
        } catch (NoSuchBeanDefinitionException ex) {
            mbeanExporter = null;
        }
        if (componentContext == null) {
            try {
                componentContext = applicationContext.getBean(ComponentContext.class);
            } catch (BeansException e) {
                //skip
            }
        }
    }

    /**
     * Publish event to the application context and all parents
     *
     * @param event the event to be published
     */
    public void publishEvent(ApplicationEvent event) {
        //向所有的context发布，context里面有防止重复的机制
        List<ApplicationContext> contexts = componentContext.getApplicationFeatures();
        //在启动过程中，当前的context还没有被注册到组件上下文已经加载的特性中
        //这会导致启动过程中发出的消息，本组件内部的其他listener反而听不到
        if (applicationContext != null && !contexts.contains(applicationContext)) {
            contexts.add(applicationContext);
        }
        ContextUtils.publishEvent(contexts, event);
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

    /**
     * un-register a mBean with specified object name
     *
     * @param name the object name of the bean
     */
    protected void unRegisterMbean(ObjectName name) {
        if( mbeanExporter != null )
            mbeanExporter.unregisterManagedResource(name);
    }

    protected static String formatViolation(Collection violations){
        StringBuilder sb = new StringBuilder();
        //noinspection unchecked
        Iterator<ConstraintViolation> it = violations.iterator();
        while (it.hasNext()) {
            ConstraintViolation violation = it.next();
            sb.append(violation.getPropertyPath()).append(" ").append(violation.getMessage());
            if( it.hasNext() ) sb.append(",");
        }
        return sb.toString();
    }

}
