/**
 * Developer: Kadvin Date: 14-6-6 下午1:08
 */
package net.happyonroad.event;

/**
 * 资源正在被修改的事件
 */
public class ObjectUpdatingEvent<Model> extends ObjectSavingEvent<Model> {
    private final Model updating;

    public ObjectUpdatingEvent(Model legacy, Model updating) {
        super(legacy);
        this.updating = updating;
    }

    public Model getUpdating() {
        return updating;
    }
}
