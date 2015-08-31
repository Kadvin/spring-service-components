package net.happyonroad.event;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.reflect.FieldUtils;
import org.springframework.context.ApplicationEvent;

/**
 * <h1>Class Title</h1>
 *
 * @author Jay Xiong
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public class AbstractEvent<Model> extends ApplicationEvent {
    private static final long serialVersionUID = 2208172696199646808L;

    @JsonCreator
    public AbstractEvent(@JsonProperty("source") Model source) {
        super(source);
    }

    @Override
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    public Model getSource() {
        //noinspection unchecked
        return (Model) super.getSource();
    }

    // FOR JSON

    /**
     * @deprecated Don't use it
     * @param timestamp the timestamp
     */
    public void setTimestamp(long timestamp){
        try {
            FieldUtils.writeField(this, "timestamp", timestamp, true);
        } catch (IllegalAccessException e) {
            //ignore it
        }
    }

}
