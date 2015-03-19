/**
 * Developer: Kadvin Date: 15/1/22 下午4:45
 */
package net.happyonroad.event;

/**
 * <h1>对象通不过校验</h1>
 */
public class ObjectValidateFailureEvent<Model>  extends ObjectFailureEvent<Model> {
    private static final long serialVersionUID = -6509507968559068462L;

    public ObjectValidateFailureEvent(Model source, Exception e) {
        super(source, e);
    }
}
