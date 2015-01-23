/**
 * Developer: Kadvin Date: 14-6-6 下午1:12
 */
package net.happyonroad.event;

/**
 * 资源创建失败的事件
 */
public class ObjectCreateFailureEvent<Model> extends ObjectFailureEvent<Model> {

    public ObjectCreateFailureEvent(Model source, Exception e) {
        super(source, e);
    }
}
