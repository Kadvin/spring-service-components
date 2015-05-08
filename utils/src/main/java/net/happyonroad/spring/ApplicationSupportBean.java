/**
 * Developer: Kadvin Date: 14-5-16 上午9:36
 */
package net.happyonroad.spring;

import net.happyonroad.component.core.ComponentContext;
import net.happyonroad.spring.support.SmartApplicationEventMulticaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.jmx.export.MBeanExportOperations;

import javax.management.ObjectName;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * The bean support application
 */
public class ApplicationSupportBean extends TranslateSupportBean
        implements ApplicationContextAware, ApplicationEventPublisher {
    private static   Logger           eventLogger = LoggerFactory.getLogger(ApplicationEvent.class);

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


        // 2. 提速
        Set<ApplicationListener<ApplicationEvent>> listeners = new HashSet<ApplicationListener<ApplicationEvent>>();
        List<ApplicationContext> contexts = componentContext.getApplicationFeatures();
        //在启动过程中，当前的context还没有被注册到组件上下文已经加载的特性中
        //这会导致启动过程中发出的消息，本组件内部的其他listener反而听不到
        if (applicationContext != null && !contexts.contains(applicationContext)) {
            contexts.add(applicationContext);
        }
        for (ApplicationContext context : contexts) {
            if( context != null ) {
                if( context instanceof ConfigurableApplicationContext){
                    if( !((ConfigurableApplicationContext) context).isActive() ) continue;
                }
                ApplicationEventMulticaster multicaster = (ApplicationEventMulticaster) context.getBean(
                        AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME);
                if( multicaster instanceof SmartApplicationEventMulticaster){
                    SmartApplicationEventMulticaster smartOne = (SmartApplicationEventMulticaster) multicaster;
                    Collection<ApplicationListener<?>> partListeners = smartOne.getApplicationListeners(event);
                    for (ApplicationListener<?> listener : partListeners) {
                        //noinspection unchecked
                        listeners.add((ApplicationListener<ApplicationEvent>) listener);
                    }
                }
            }
        }
        if( !listeners.isEmpty() ){
            LinkedList<ApplicationListener<ApplicationEvent>> list = new LinkedList<ApplicationListener<ApplicationEvent>>();
            //noinspection unchecked
            list.addAll(listeners);
            OrderComparator.sort(list);
            if( eventLogger.isInfoEnabled() ){
                StringBuilder sb = new StringBuilder();
                Iterator<ApplicationListener<ApplicationEvent>> it = list.iterator();
                while (it.hasNext()) {
                    ApplicationListener<ApplicationEvent> listener = it.next();
                    sb.append(listener.getClass().getSimpleName()).append("(");
                    if( listener instanceof Ordered ){
                        sb.append(((Ordered) listener).getOrder());
                    }else{
                        sb.append("0");
                    }
                    sb.append(")");
                    if( it.hasNext() ) sb.append(",");
                }
                eventLogger.info("Publish {} of {} to {}", event.getClass().getSimpleName(),
                                 event.getSource().getClass().getSimpleName(), sb);
            }
            for (ApplicationListener<ApplicationEvent> listener : list) {
                listener.onApplicationEvent(event);
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
