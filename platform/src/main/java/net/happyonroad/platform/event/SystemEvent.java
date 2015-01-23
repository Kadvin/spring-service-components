/**
 * Developer: Kadvin Date: 14-5-16 上午9:46
 */
package net.happyonroad.platform.event;

import org.springframework.context.ApplicationEvent;

/**
 * 所有服务包的事件
 */
public class SystemEvent extends ApplicationEvent {
    public SystemEvent(Object source) {
        super(source);
    }

}
