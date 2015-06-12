/**
 * Developer: Kadvin Date: 14-7-16 上午10:47
 */
package net.happyonroad.platform.support;

import net.happyonroad.spring.Bean;
import net.happyonroad.spring.context.ContextUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Forward application event from platform context to spring mvc context
 *
 * 注意：要求构建该对象的 application context具有防止接收重复事件的能力
 * 参考： SmartApplicationEventMulticaster
 */
@Component
class PlatformEventForwarder extends Bean
        implements ApplicationListener<ApplicationEvent> {
    ApplicationContext[]                contexts;
    Class<? extends ApplicationEvent>[] eventClasses;

    public PlatformEventForwarder() {
        setOrder(1000);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (eventClasses == null) return;
        if (eventClasses.length == 0) return;
        if (contexts == null) return;
        if (contexts.length == 0) return;

        boolean pass = false;
        for (Class<? extends ApplicationEvent> eventClass : eventClasses) {
            if (eventClass.isAssignableFrom(event.getClass())) {
                pass = true;
                break;
            }
        }
        if (!pass) return;
        ContextUtils.publishEvent(Arrays.asList(contexts), event);
    }

    public void bind(ApplicationContext... targets){
        this.contexts = targets;
    }

    public void forward(Class... classes){
        //noinspection unchecked
        this.eventClasses = classes;
    }
}
