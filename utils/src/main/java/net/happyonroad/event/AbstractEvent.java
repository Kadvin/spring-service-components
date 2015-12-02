package net.happyonroad.event;

import com.fasterxml.jackson.annotation.*;
import net.happyonroad.support.DefaultGeneralMap;
import org.apache.commons.lang.reflect.FieldUtils;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>Class Title</h1>
 *
 * @author Jay Xiong
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public class AbstractEvent<Model> extends ApplicationEvent {
    private static final long serialVersionUID = 2208172696199646808L;

    private Map<String, Object> attributes;

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

    public void setAttribute(String key, Object value) {
        if(attributes == null ) attributes = new DefaultGeneralMap<String, Object>();
        attributes.put(key, value);
    }


    public <T> T getAttribute(String key) {
        if( attributes == null ) return null;
        //noinspection unchecked
        return (T)attributes.get(key);
    }
}
