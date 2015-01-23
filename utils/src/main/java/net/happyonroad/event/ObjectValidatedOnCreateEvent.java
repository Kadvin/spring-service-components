/**
 * Developer: Kadvin Date: 15/1/22 下午4:45
 */
package net.happyonroad.event;

/**
 * <h1>新建的对象通过校验</h1>
 */
public class ObjectValidatedOnCreateEvent<Model>  extends ObjectValidatedEvent<Model> {
    public ObjectValidatedOnCreateEvent(Model source) {
        super(source);
    }
}
