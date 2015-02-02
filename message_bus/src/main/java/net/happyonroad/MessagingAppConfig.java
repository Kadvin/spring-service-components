/**
 * Developer: Kadvin Date: 15/2/2 下午5:09
 */
package net.happyonroad;

import net.happyonroad.component.container.ServiceExporter;
import net.happyonroad.component.container.ServiceImporter;
import net.happyonroad.concurrent.StrategyExecutorService;
import net.happyonroad.messaging.MessageBus;
import net.happyonroad.messaging.support.DefaultMessageBus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.TaskScheduler;

/**
 * <h1>Messaging程序的App Config</h1>
 */
@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class MessagingAppConfig implements InitializingBean{
    @Autowired
    ServiceImporter importer;
    @Autowired
    ServiceExporter exporter;
    @Bean
    MessageBus defaultMessageBus(){
        return new DefaultMessageBus();
    }

    @Bean
    TaskScheduler timeoutScheduler(){
        return importer.imports(TaskScheduler.class, "system");
    }

    @Bean
    StrategyExecutorService messagingPoolExecutor(){
        return new StrategyExecutorService("messaging");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        exporter.exports(MessageBus.class, defaultMessageBus());
    }
}
