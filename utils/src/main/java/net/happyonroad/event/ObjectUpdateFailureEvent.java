/**
 * Developer: Kadvin Date: 14-6-6 下午1:12
 */
package net.happyonroad.event;

/**
 * 资源修改失败的事件
 */
public class ObjectUpdateFailureEvent<Model> extends ObjectFailureEvent<Model> {

    private static final long serialVersionUID = -3340641946100791940L;
    private final Model updating;

    public ObjectUpdateFailureEvent(Model source, Model updating, Exception e) {
        super(source, e);
        this.updating = updating;
    }

    public Model getUpdating() {
        return updating;
    }
}
