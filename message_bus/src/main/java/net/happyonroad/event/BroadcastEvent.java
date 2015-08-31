package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.util.StringUtils;

/**
 * <h1>进程之间的事件</h1>
 *
 * @author Jay Xiong
 */
public class BroadcastEvent<Model> extends AbstractEvent<Model> {
    private static final long serialVersionUID = 1607061315920556154L;
    private String appIndex;

    public BroadcastEvent(@JsonProperty("source") Model source) {
        super(source);
        this.appIndex = System.getProperty("app.index");
    }

    public String getAppIndex() {
        return appIndex;
    }

    public void setAppIndex(String appIndex) {
        this.appIndex = appIndex;
    }

    @JsonIgnore
    public boolean isNative(){
        return StringUtils.equals(getAppIndex(), System.getProperty("app.index"));
    }

    public static <M> BroadcastEvent<M> broadcast(ObjectEvent<M> origin) {
        BroadcastEvent<M> event;
        if (origin instanceof ObjectCreatedEvent) {
            event = new ObjectCreatedBroadcastEvent<M>(origin.getSource());
        } else if (origin instanceof ObjectDestroyedEvent) {
            event = new ObjectDestroyedBroadcastEvent<M>(origin.getSource());
        } else if (origin instanceof ObjectUpdatedEvent) {
            ObjectUpdatedEvent<M> updatedEvent = (ObjectUpdatedEvent<M>) origin;
            event = new ObjectUpdatedBroadcastEvent<M>(updatedEvent.getSource(), updatedEvent.getLegacy());
        } else {
            throw new UnsupportedOperationException("Can't convert " + origin + " as broadcast event now");
        }
        return event;
    }
}
