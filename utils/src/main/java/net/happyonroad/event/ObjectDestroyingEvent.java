/**
 * Developer: Kadvin Date: 14-6-6 下午1:03
 */
package net.happyonroad.event;

/**
 * 资源即将被删除的事件
 */
public class ObjectDestroyingEvent<Model> extends ObjectEvent<Model> {
    public ObjectDestroyingEvent(Model source) {
        super(source);
    }
}
