/**
 * Developer: Kadvin Date: 15/1/22 下午4:45
 */
package net.happyonroad.event;

/**
 * <h1>新建对象正在被校验</h1>
 */
public class ObjectValidatingOnCreateEvent<Model>  extends ObjectValidatingEvent<Model> {
    private static final long serialVersionUID = 361093405417849932L;

    public ObjectValidatingOnCreateEvent(Model source) {
        super(source);
    }
}
