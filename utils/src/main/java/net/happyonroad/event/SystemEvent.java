/**
 * Developer: Kadvin Date: 14-5-16 上午9:46
 */
package net.happyonroad.event;

import net.happyonroad.component.core.ComponentContext;
import org.springframework.context.ApplicationEvent;

/**
 * 所有服务包的事件
 */
public class SystemEvent extends ApplicationEvent {
    private static final long serialVersionUID = -3156784322713769605L;

    public SystemEvent(ComponentContext source) {
        super(source);
    }

    @Override
    public ComponentContext getSource() {
        return (ComponentContext) super.getSource();
    }
}
