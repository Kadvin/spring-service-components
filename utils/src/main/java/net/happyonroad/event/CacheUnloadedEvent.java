package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.model.Record;

/**
 * <h1>记录已经卸载事件</h1>
 *
 * @author Jay Xiong
 */
public class CacheUnloadedEvent<T extends Record> extends CacheOperationEvent<T> {

    private static final long serialVersionUID = 5985487901249629920L;

    public CacheUnloadedEvent(@JsonProperty("source") T source) {
        super(source);
    }
}
