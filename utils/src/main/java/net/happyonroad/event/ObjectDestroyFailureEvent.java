/**
 * Developer: Kadvin Date: 14-6-6 下午1:12
 */
package net.happyonroad.event;

/**
 * 资源删除失败的事件
 */
public class ObjectDestroyFailureEvent<Model> extends ObjectFailureEvent<Model> {

    public ObjectDestroyFailureEvent(Model source, Exception e) {
        super(source, e);
    }
}
