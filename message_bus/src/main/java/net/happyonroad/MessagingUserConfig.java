/**
 * Developer: Kadvin Date: 15/2/3 上午9:40
 */
package net.happyonroad;

import net.happyonroad.messaging.MessageBus;
import net.happyonroad.spring.config.AbstractUserConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>使用MessageBus的模块需要import的Configuration</h1>
 *
 * Import的未必是当前包提供的实现，是根据配置参数 messaging.provider 决定
 */
@Configuration
public class MessagingUserConfig extends AbstractUserConfig{

    @Bean
    public MessageBus messageBus(){
        return imports(MessageBus.class, System.getProperty("messaging.provider", "default"));
    }
}
