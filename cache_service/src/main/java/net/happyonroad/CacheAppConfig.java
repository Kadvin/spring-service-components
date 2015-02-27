/**
 * Developer: Kadvin Date: 15/2/2 下午5:02
 */
package net.happyonroad;

import net.happyonroad.cache.MutableCacheService;
import net.happyonroad.spring.config.AbstractAppConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * <h1>Cache组件的AppConfig</h1>
 */
@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@ComponentScan("net.happyonroad.cache.support")
public class CacheAppConfig extends AbstractAppConfig{

    @Override
    public void doExports() {
        exports(MutableCacheService.class, "default");
    }
}
