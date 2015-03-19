/**
 * Developer: Kadvin Date: 15/1/22 下午4:45
 */
package net.happyonroad.event;

/**
 * <h1>对象通过校验</h1>
 */
public class ObjectValidatedEvent<Model>  extends ObjectEvent<Model> {
    private static final long serialVersionUID = -4657553062321812243L;

    public ObjectValidatedEvent(Model source) {
        super(source);
    }
}
