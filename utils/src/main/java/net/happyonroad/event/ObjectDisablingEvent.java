package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <h1>Object Disabling Event</h1>
 *
 * @author Jay Xiong
 */
public class ObjectDisablingEvent<Model> extends ObjectEvent<Model>{
    private static final long serialVersionUID = 5026602389727218250L;

    public ObjectDisablingEvent(@JsonProperty("source") Model source) {
        super(source);
    }
}
