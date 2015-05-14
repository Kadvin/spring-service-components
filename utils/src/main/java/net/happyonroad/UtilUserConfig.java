/**
 * Developer: Kadvin Date: 15/2/3 上午10:20
 */
package net.happyonroad;

import net.happyonroad.service.ExtensionContainer;
import net.happyonroad.spring.config.AbstractUserConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

/**
 * <h1>Utility's用者导入配置</h1>
 */
@Configuration
public class UtilUserConfig extends AbstractUserConfig {
    @Bean
    TaskScheduler systemTaskScheduler() {
        return imports(TaskScheduler.class, "system");
    }

    @Bean
    ExtensionContainer extensionContainer(){
        return imports(ExtensionContainer.class);
    }
}
