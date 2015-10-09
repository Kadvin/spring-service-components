/**
 * Developer: Kadvin Date: 14-6-6 下午1:03
 */
package net.happyonroad.event;

/**
 * 资源已经被删除的事件
 */
public class ObjectDestroyedEvent<Model> extends ObjectSavedEvent<Model> {
    private static final long serialVersionUID = -8153623340067463984L;

    public ObjectDestroyedEvent(Model source) {
        super(source);
    }
}
