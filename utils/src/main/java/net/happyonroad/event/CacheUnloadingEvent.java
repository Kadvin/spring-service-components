package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.model.Record;

/**
 * <h1>记录即将卸载事件</h1>
 *
 * @author Jay Xiong
 */
public class CacheUnloadingEvent<T extends Record> extends CacheOperationEvent<T> {

    private static final long serialVersionUID = 8220697833396476037L;

    public CacheUnloadingEvent(@JsonProperty("source") T source) {
        super(source);
    }
}
