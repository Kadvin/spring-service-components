/**
 * Developer: Kadvin Date: 15/1/22 下午4:45
 */
package net.happyonroad.event;

/**
 * <h1>对象通不过校验</h1>
 */
public class ObjectValidateOnUpdateFailureEvent<Model>  extends ObjectValidateFailureEvent<Model> {
    private final Model updated;

    public ObjectValidateOnUpdateFailureEvent(Model source, Model updated, Exception e) {
        super(source, e);
        this.updated = updated;
    }

    public Model getUpdated() {
        return updated;
    }
}
