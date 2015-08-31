package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <h1>对象删除之后的进程间事件</h1>
 *
 * @author Jay Xiong
 */
public class ObjectDestroyedBroadcastEvent<Model> extends BroadcastEvent<Model> {
    private static final long serialVersionUID = -1640650424736915321L;

    public ObjectDestroyedBroadcastEvent(@JsonProperty("source") Model source) {
        super(source);
    }
}
