/**
 * Developer: Kadvin Date: 14-6-6 下午1:03
 */
package net.happyonroad.event;

/**
 * 资源已经被删除的事件
 */
public class ObjectDestroyedEvent<Model> extends ObjectEvent<Model> {
    public ObjectDestroyedEvent(Model source) {
        super(source);
    }
}
