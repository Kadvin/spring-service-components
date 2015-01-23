/**
 * Developer: Kadvin Date: 15/1/22 下午4:45
 */
package net.happyonroad.event;

/**
 * <h1>对象通不过校验</h1>
 */
public class ObjectValidateOnCreateFailureEvent<Model>  extends ObjectValidateFailureEvent<Model> {
    public ObjectValidateOnCreateFailureEvent(Model source, Exception e) {
        super(source, e);
    }
}
