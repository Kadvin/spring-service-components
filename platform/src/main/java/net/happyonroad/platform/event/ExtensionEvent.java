/**
 * Developer: Kadvin Date: 14-5-16 上午9:46
 */
package net.happyonroad.platform.event;

import net.happyonroad.component.core.Component;
import org.springframework.context.ApplicationEvent;

/**
 * 所有服务包的事件
 */
public class ExtensionEvent extends ApplicationEvent {

    public ExtensionEvent(Component source) {
        super(source);
    }

    @Override
    public Component getSource() {
        return (Component) super.getSource();
    }


}
