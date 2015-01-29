/**
 * Developer: Kadvin Date: 14-7-16 上午10:47
 */
package net.happyonroad.platform.support;

import net.happyonroad.spring.Bean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Forward application event from platform context to spring mvc context
 *
 * 注意：要求构建该对象的 application context具有防止接收重复事件的能力
 * 参考： SmartApplicationEventMulticaster
 */
public class PlatformEventForwarder extends Bean
        implements ApplicationListener<ApplicationEvent> {
    ApplicationContext[] contexts;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (contexts == null) return;
        if (contexts.length == 0) return;
        for (ApplicationContext context : contexts) {
            context.publishEvent(event);
        }
    }

    public void bind(ApplicationContext... targets){
        this.contexts = targets;
    }
}
