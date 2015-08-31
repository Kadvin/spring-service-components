package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <h1>对象创建之后的进程间事件</h1>
 *
 * @author Jay Xiong
 */
public class ObjectUpdatedBroadcastEvent<Model> extends BroadcastEvent<Model> {
    private static final long serialVersionUID = -1640650424736915321L;
    private final Object legacy;

    public ObjectUpdatedBroadcastEvent(@JsonProperty("source") Model source, @JsonProperty("legacy") Object legacy) {
        super(source);
        this.legacy = legacy;
    }

    // Legacy对象不能为特定类型，因为有可能是类型细化
    public Object getLegacy() {
        return legacy;
    }

    @JsonIgnore
    public Model getLegacySource(){
        return (Model) legacy;
    }
}
