/**
 * Developer: Kadvin Date: 15/2/3 上午9:40
 */
package net.happyonroad;

import net.happyonroad.cache.CacheService;
import net.happyonroad.spring.config.AbstractUserConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>使用CacheService的模块需要import的Configuration</h1>
 * Import的未必是当前包提供的实现，是根据配置参数 cache.provider 决定
 */
@Configuration
public class CacheUserConfig extends AbstractUserConfig{
    @Bean
    public CacheService cacheService(){
        return imports(CacheService.class, System.getProperty("cache.provider", "default"));
    }
}
