package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <h1>Object Disabled Event</h1>
 *
 * @author Jay Xiong
 */
public class ObjectDisabledEvent<Model> extends ObjectEvent<Model>{
    private static final long serialVersionUID = 5026602389727218250L;

    public ObjectDisabledEvent(@JsonProperty("source") Model source) {
        super(source);
    }
}
