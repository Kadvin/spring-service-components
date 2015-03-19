/**
 * Developer: Kadvin Date: 14-6-6 下午1:08
 */
package net.happyonroad.event;

/**
 * 资源已经被创建
 */
public class ObjectCreatedEvent<Model> extends ObjectSavedEvent<Model> {
    private static final long serialVersionUID = -7500396821991824806L;
    public ObjectCreatedEvent(Model source) {
        super(source);
    }
}
