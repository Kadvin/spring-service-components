/**
 * Developer: Kadvin Date: 14-6-6 下午1:08
 */
package net.happyonroad.event;

/**
 * <h1>对象正在被修改的事件</h1>
 * 其source为正在被修改的对象，updating为即将被改成的对象，两者类型可能不同
 */
public class ObjectUpdatingEvent<Model> extends ObjectSavingEvent<Model> {
    private static final long serialVersionUID = -5863399614640950187L;
    private final Model updating;

    public ObjectUpdatingEvent(Model legacy, Model updating) {
        super(legacy);
        this.updating = updating;
    }

    public Model getUpdating() {
        return updating;
    }
}
