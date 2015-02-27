/**
 * Developer: Kadvin Date: 15/2/2 下午5:02
 */
package net.happyonroad;

import net.happyonroad.cache.MutableCacheService;
import net.happyonroad.spring.config.AbstractAppConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>Cache组件的AppConfig</h1>
 */
@Configuration
@ComponentScan("net.happyonroad.cache.support")
public class CacheAppConfig extends AbstractAppConfig{

    @Override
    public void doExports() {
        exports(MutableCacheService.class, "default");
    }
}
