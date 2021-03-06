/**
 * @author XiongJie, Date: 13-11-14
 */
package net.happyonroad.remoting;

import net.happyonroad.cache.ListChannel;
import net.happyonroad.cache.MutableCacheService;

/**
 * A Remote List Channel
 */
public class RemoteListChannel implements ListChannel {
    private MutableCacheService cacheService;
    private String              name;

    public RemoteListChannel(MutableCacheService cacheService, String name) {
        this.cacheService = cacheService;
        this.name = name;
    }

    @Override
    public byte[] blockPopRight(int timeout) {
        return cacheService.blockPopRightFromList(name, timeout);
    }

    @Override
    public void pushLeft(byte[] value) {
        cacheService.pushLeftToList(name, value);
    }
}
