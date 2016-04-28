package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.model.Record;

/**
 * <h1>记录即将重新加载事件</h1>
 *
 * @author Jay Xiong
 */
public class CacheReloadingEvent<T extends Record> extends CacheOperationEvent<T> {

    private static final long serialVersionUID = -3028167087280718790L;

    public CacheReloadingEvent(@JsonProperty("source") T source) {
        super(source);
    }
}
