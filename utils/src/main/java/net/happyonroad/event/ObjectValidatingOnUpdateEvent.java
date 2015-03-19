/**
 * Developer: Kadvin Date: 15/1/22 下午4:45
 */
package net.happyonroad.event;

/**
 * <h1>更新对象正在被校验</h1>
 */
public class ObjectValidatingOnUpdateEvent<Model>  extends ObjectValidatingEvent<Model> {
    private static final long serialVersionUID = -9101889826361749971L;
    private final Model updated;

    public ObjectValidatingOnUpdateEvent(Model source, Model updated) {
        super(source);
        this.updated = updated;
    }

    public Model getUpdated() {
        return updated;
    }
}
