/**
 * Developer: Kadvin Date: 14-9-23 上午8:25
 */
package net.happyonroad;

import net.happyonroad.support.DefaultSystemInvoker;
import net.happyonroad.support.SystemInvokeManager;
import net.happyonroad.util.NamedThreadFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Spring configuration of System Invoker Manager
 */
@Configuration
public class SystemInvokeConfig {
    @Bean
    @Qualifier("systemInvokeExecutor")
    public ExecutorService executorService(){
        return Executors.newFixedThreadPool(10, new NamedThreadFactory("SystemInvokeExecutor")) ;
    }

    @Bean
    public DefaultSystemInvoker systemInvoker(){
        return new DefaultSystemInvoker();
    }

    @Bean
    public SystemInvokeManager systemInvokeManager(){
        return new SystemInvokeManager();
    }

    @Bean
    public TaskScheduler cleanScheduler(){
        return new ThreadPoolTaskScheduler();
    }

}
