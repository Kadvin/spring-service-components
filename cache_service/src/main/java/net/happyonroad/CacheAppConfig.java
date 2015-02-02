/**
 * Developer: Kadvin Date: 15/2/2 下午5:02
 */
package net.happyonroad;

import net.happyonroad.cache.MutableCacheService;
import net.happyonroad.cache.support.DefaultCache;
import net.happyonroad.component.container.ServiceExporter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * <h1>Cache组件的AppConfig</h1>
 */
@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class CacheAppConfig implements InitializingBean{
    @Autowired
    ServiceExporter exporter;

    @Bean
    public DefaultCache defaultCache(){
        return DefaultCache.getSharedInstance();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        exporter.exports(MutableCacheService.class, defaultCache(), "default");
    }
}
