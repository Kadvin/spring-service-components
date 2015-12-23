/**
 * Developer: Kadvin Date: 14-9-18 上午10:40
 */
package net.happyonroad;

import net.happyonroad.service.SystemInvokeService;
import net.happyonroad.concurrent.StrategyExecutorService;
import net.happyonroad.spring.config.AbstractAppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;

/**
 * <h1>系统调用模块的应用配置</h1>
 */
@Configuration
@ComponentScan("net.happyonroad.support")
@Import(UtilUserConfig.class)
public class SystemScriptAppConfig extends AbstractAppConfig {
    @Override
    protected void doExports() {
        super.doExports();
        exports(SystemInvokeService.class);
    }

    @Bean
    public ExecutorService systemInvokeExecutor(){
        return new StrategyExecutorService("system.invoke", "SystemInvocation");
    }
}
