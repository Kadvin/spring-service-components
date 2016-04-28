package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.model.Record;

/**
 * <h1>记录已经重新加载事件</h1>
 *
 * @author Jay Xiong
 */
public class CacheReloadedEvent<T extends Record> extends CacheOperationEvent<T> {

    private static final long serialVersionUID = -3291902167304848840L;

    public CacheReloadedEvent(@JsonProperty("source") T source) {
        super(source);
    }
}
