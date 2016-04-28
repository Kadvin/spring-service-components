package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <h1>Object Enabled Event</h1>
 *
 * @author Jay Xiong
 */
public class ObjectEnabledEvent<Model> extends ObjectEvent<Model>{
    private static final long serialVersionUID = 5026602389727218250L;

    public ObjectEnabledEvent(@JsonProperty("source") Model source) {
        super(source);
    }
}
