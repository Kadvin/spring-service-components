/**
 * Developer: Kadvin Date: 15/2/2 下午5:17
 */
package net.happyonroad;

import net.happyonroad.config.ApplyToSystemProperties;
import net.happyonroad.spring.config.AbstractAppConfig;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * <h1>Util App Config</h1>
 */
@Configuration
// context:property-placeholder 里面的 location 包括通配符的功能
//  通过 @PropertySource 无法实现
@ImportResource("classpath:META-INF/properties.xml")
//@PropertySource("file://${app.home}/config/${app.config}")
public class UtilAppConfig extends AbstractAppConfig {

    @Bean
    ApplyToSystemProperties applyToSystemProperties(){
        return new ApplyToSystemProperties();
    }

    @Bean
    TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadGroupName("SystemScheduler");
        scheduler.setThreadNamePrefix("SystemScheduler-");
        return scheduler;
    }

    @Override
    public void doExports()  {
        exports(TaskScheduler.class, "system");
    }

    @Override
    protected void afterExports() {
        // 为了支持 @PropertySource + @Value 联合工作，必须有这个对象
        PropertyResourceConfigurer configurer = applicationContext.getBean(PropertyResourceConfigurer.class);
        //在父上下文中创建一个 PropertyResourceConfigurer 注册到 parent context中
        // 以便其他模块在使用 ContextUtils.inheritParentProperties 时能够找到一个可用的
        ApplicationContext parent = applicationContext.getParent();
        ((ConfigurableApplicationContext)parent).addBeanFactoryPostProcessor(configurer);
    }
}
