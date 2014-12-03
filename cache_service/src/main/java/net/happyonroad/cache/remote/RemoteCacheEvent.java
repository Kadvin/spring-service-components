/**
 * @author XiongJie, Date: 13-11-11
 */
package net.happyonroad.cache.remote;

import org.springframework.context.ApplicationEvent;

/** Description */
public class RemoteCacheEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1671003500259931090L;

    public RemoteCacheEvent(Object source) {
        super(source);
    }
}
