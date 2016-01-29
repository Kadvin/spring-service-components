/**
 * Developer: Kadvin Date: 14-9-18 上午10:40
 */
package net.happyonroad;

import net.happyonroad.service.SystemInvokeService;
import net.happyonroad.spring.config.AbstractUserConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>系统调用模块的使用者导入配置</h1>
 */
@Configuration
public class SystemScriptUserConfig extends AbstractUserConfig {
    @Bean
    public SystemInvokeService systemInvokeService() {
        return imports(SystemInvokeService.class);
    }
}
