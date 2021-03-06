/**
 * Developer: Kadvin Date: 14-6-6 下午1:08
 */
package net.happyonroad.event;

/**
 * 资源已经被保存(创建或修改)
 */
public class ObjectSavedEvent<Model> extends ObjectEvent<Model> {
    private static final long serialVersionUID = 2135196646215118050L;

    public ObjectSavedEvent(Model source) {
        super(source);
    }
}
