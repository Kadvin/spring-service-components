/**
 * Developer: Kadvin Date: 14-6-6 下午1:08
 */
package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <h2>对象已经被修改</h2>
 * 其source为正在已经被修改好的对象，legacy为原先的对象，两者类型可能不同
 */
public class ObjectUpdatedEvent<Model> extends ObjectSavedEvent<Model> {
    private static final long serialVersionUID = -6357123648516562149L;
    private final Object legacy;

    @JsonCreator
    public ObjectUpdatedEvent(@JsonProperty("source") Model source, @JsonProperty("legacy") Object legacy) {
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
