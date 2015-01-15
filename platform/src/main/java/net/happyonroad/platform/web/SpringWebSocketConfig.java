/**
 * Developer: Kadvin Date: 15/1/14 下午5:29
 */
package net.happyonroad.platform.web;

import net.happyonroad.platform.web.handler.NorthHandler;
import net.happyonroad.platform.web.handler.SouthHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * The spring web socket configuration
 */
@Configuration
@EnableWebSocket
public class SpringWebSocketConfig  implements WebSocketConfigurer{
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(southHandler(), "/south")
                .addHandler(northHandler(), "/north").withSockJS();
    }

    @Bean
    public WebSocketHandler southHandler() {
        return new SouthHandler();
    }

    private WebSocketHandler northHandler() {
        return new NorthHandler();
    }

}
