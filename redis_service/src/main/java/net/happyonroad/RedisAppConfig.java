/**
 * Developer: Kadvin Date: 15/2/2 下午5:09
 */
package net.happyonroad;

import net.happyonroad.cache.CacheService;
import net.happyonroad.messaging.MessageBus;
import net.happyonroad.spring.config.AbstractAppConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>Messaging程序的App Config</h1>
 */
@Configuration
@ComponentScan("net.happyonroad.redis")
public class RedisAppConfig extends AbstractAppConfig{
    @Override
    public void doExports() {
        exports(MessageBus.class, "redis");
        exports(CacheService.class, "redis");
    }
}
