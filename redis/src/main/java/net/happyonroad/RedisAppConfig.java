/**
 * Developer: Kadvin Date: 15/2/2 下午5:09
 */
package net.happyonroad;

import net.happyonroad.cache.CacheService;
import net.happyonroad.component.container.ServiceExporter;
import net.happyonroad.component.container.ServiceImporter;
import net.happyonroad.messaging.MessageBus;
import net.happyonroad.redis.RedisCache;
import net.happyonroad.redis.RedisMessageBus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * <h1>Messaging程序的App Config</h1>
 */
@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@ComponentScan("net.happyonroad.redis")
public class RedisAppConfig implements InitializingBean{
    @Autowired
    ServiceImporter importer;
    @Autowired
    ServiceExporter exporter;

    @Autowired
    RedisMessageBus redisMessageBus;
    @Autowired
    RedisCache redisCache;

    @Override
    public void afterPropertiesSet() throws Exception {
        exporter.exports(MessageBus.class, redisMessageBus, "redis");
        exporter.exports(CacheService.class, redisCache, "redis");
    }
}
