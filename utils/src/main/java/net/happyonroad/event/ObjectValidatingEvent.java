/**
 * Developer: Kadvin Date: 15/1/22 下午4:45
 */
package net.happyonroad.event;

/**
 * <h1>对象正在被校验</h1>
 */
public class ObjectValidatingEvent<Model>  extends ObjectEvent<Model> {
    public ObjectValidatingEvent(Model source) {
        super(source);
    }
}
