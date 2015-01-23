/**
 * Developer: Kadvin Date: 14-6-6 下午1:08
 */
package net.happyonroad.event;

/**
 * 资源已经被修改
 */
public class ObjectUpdatedEvent<Model> extends ObjectSavedEvent<Model> {
    private final Model updating;

    public ObjectUpdatedEvent(Model source, Model updating) {
        super(source);
        this.updating = updating;
    }

    public Model getUpdating() {
        return updating;
    }
}
