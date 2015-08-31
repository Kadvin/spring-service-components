package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <h1>对象创建之后的进程间事件</h1>
 *
 * @author Jay Xiong
 */
public class ObjectCreatedBroadcastEvent<Model> extends BroadcastEvent<Model> {
    private static final long serialVersionUID = -1640650424736915321L;

    public ObjectCreatedBroadcastEvent(@JsonProperty("source") Model source) {
        super(source);
    }
}
