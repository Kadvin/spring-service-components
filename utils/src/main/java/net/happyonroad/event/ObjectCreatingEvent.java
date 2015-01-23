/**
 * Developer: Kadvin Date: 14-6-6 下午1:08
 */
package net.happyonroad.event;

/**
 * 资源正在被创建的事件
 */
public class ObjectCreatingEvent<Model> extends ObjectSavingEvent<Model> {
    public ObjectCreatingEvent(Model source) {
        super(source);
    }
}
