/**
 * @author XiongJie, Date: 13-11-11
 */
package dnt.cache.remote;

/** Remote Cache Connected */
public class RemoteCacheConnectedEvent extends RemoteCacheEvent {
    private static final long serialVersionUID = -2329926438143330434L;

    public RemoteCacheConnectedEvent(Object source) {
        super(source);
    }
}
