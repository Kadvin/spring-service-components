/**
 * Developer: Kadvin Date: 15/2/2 下午5:02
 */
package net.happyonroad;

import net.happyonroad.cache.MutableCacheService;
import net.happyonroad.cache.support.DefaultCache;
import net.happyonroad.spring.config.AbstractAppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * <h1>Cache组件的AppConfig</h1>
 */
@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class CacheAppConfig extends AbstractAppConfig{

    @Bean
    public DefaultCache defaultCache(){
        return DefaultCache.getSharedInstance();
    }

    @Override
    public void doExports() {
        exports(MutableCacheService.class, "default");
    }
}
