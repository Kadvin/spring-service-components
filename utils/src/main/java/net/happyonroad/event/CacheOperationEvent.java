package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.event.ObjectEvent;
import net.happyonroad.model.Record;

/**
 * <h1>缓存的对象管理事件</h1>
 *
 * @author Jay Xiong
 */
public class CacheOperationEvent<T extends Record> extends ObjectEvent<T> {

    private static final long serialVersionUID = -1504540206400248145L;

    public CacheOperationEvent(@JsonProperty("source") T source) {
        super(source);
    }
}
