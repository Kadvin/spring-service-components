/**
 * Developer: Kadvin Date: 14-6-6 下午1:03
 */
package net.happyonroad.event;

import org.springframework.context.ApplicationEvent;

/**
 * <h1>基于Parameter Type的资源事件对象</h1>
 *
 * <pre>
 * 代码中编写时，事件的生成顺序应该为：
 *   |-> Creating
 *   |-> Created  | CreateFailure
 *
 *   |-> Updating
 *   |-> Updated  | UpdateFailure
 *
 *   |-> Destroying
 *   |-> Destroyed  | DestroyFailure
 * </pre>
 *
 * 事件的接收原则为：
 * <ol>
 * <li>先按照依赖顺序对组件进行排序，被依赖的先被通知
 * <li>在同一个组件内的事件监听器，如果实现了PriorityOrder接口，则按照getOrder进行排序
 * <li>如果没有实现该接口，按照名称自然排序
 * </ol>
 *
 */
public class ObjectEvent<Model> extends ApplicationEvent{
    public ObjectEvent(Model source) {
        super(source);
    }

    @Override
    public Model getSource() {
        //noinspection unchecked
        return (Model) super.getSource();
    }
}
