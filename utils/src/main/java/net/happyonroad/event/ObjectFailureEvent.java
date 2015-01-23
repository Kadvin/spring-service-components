/**
 * Developer: Kadvin Date: 14-6-6 下午1:12
 */
package net.happyonroad.event;

/**
 * 资源操作(创建/修改/删除)失败的事件
 */
public class ObjectFailureEvent<Model> extends ObjectEvent<Model> {
    private final Exception error;

    public ObjectFailureEvent(Model source, Exception e) {
        super(source);
        error = e;
    }

    public Exception getError() {
        return error;
    }
}
