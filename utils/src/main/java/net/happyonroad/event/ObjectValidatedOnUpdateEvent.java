/**
 * Developer: Kadvin Date: 15/1/22 下午4:45
 */
package net.happyonroad.event;

/**
 * <h1>更新的对象通过校验</h1>
 */
public class ObjectValidatedOnUpdateEvent<Model>  extends ObjectValidatedEvent<Model> {
    private static final long serialVersionUID = -3255120169889831173L;
    private final Model updated;

    public ObjectValidatedOnUpdateEvent(Model source, Model updated) {
        super(source);
        this.updated = updated;
    }

    public Model getUpdated() {
        return updated;
    }
}
