/**
 * Developer: Kadvin Date: 14-6-6 下午1:08
 */
package net.happyonroad.event;

/**
 * 资源正在被保存(创建或修改)的事件
 */
public class ObjectSavingEvent<Model> extends ObjectEvent<Model> {
    private static final long serialVersionUID = 888291044530236044L;

    public ObjectSavingEvent(Model source) {
        super(source);
    }
}
