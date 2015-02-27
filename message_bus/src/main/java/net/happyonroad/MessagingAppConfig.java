/**
 * Developer: Kadvin Date: 15/2/2 下午5:09
 */
package net.happyonroad;

import net.happyonroad.concurrent.StrategyExecutorService;
import net.happyonroad.messaging.MessageBus;
import net.happyonroad.spring.config.AbstractAppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.TaskScheduler;

/**
 * <h1>Messaging程序的App Config</h1>
 */
@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@ComponentScan("net.happyonroad.messaging.support")
public class MessagingAppConfig extends AbstractAppConfig{
    @Bean
    TaskScheduler timeoutScheduler(){
        return imports(TaskScheduler.class, "system");
    }

    @Bean
    StrategyExecutorService messagingPoolExecutor(){
        return new StrategyExecutorService("messaging");
    }

    @Override
    public void doExports() {
        exports(MessageBus.class);
    }
}
