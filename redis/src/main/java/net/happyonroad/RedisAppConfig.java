/**
 * Developer: Kadvin Date: 15/2/2 下午5:09
 */
package net.happyonroad;

import net.happyonroad.cache.CacheService;
import net.happyonroad.messaging.MessageBus;
import net.happyonroad.redis.RedisCache;
import net.happyonroad.redis.RedisMessageBus;
import net.happyonroad.spring.config.AbstractAppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * <h1>Messaging程序的App Config</h1>
 */
@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class RedisAppConfig extends AbstractAppConfig{
    @Bean
    RedisMessageBus redisMessageBus(){
        return new RedisMessageBus();
    }

    @Bean
    RedisCache redisCache(){
        return new RedisCache();
    }

    @Override
    public void doExports() {
        exports(MessageBus.class, "redis");
        exports(CacheService.class, "redis");
    }
}
