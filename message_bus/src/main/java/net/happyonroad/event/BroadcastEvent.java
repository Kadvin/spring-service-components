package net.happyonroad.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.model.Record;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * <h1>进程之间的事件</h1>
 *
 * @author Jay Xiong
 */
public class BroadcastEvent<Model> extends AbstractEvent<Model> {
    private static final long serialVersionUID = 1607061315920556154L;

    public BroadcastEvent(@JsonProperty("source") Model source) {
        super(source);
    }

    public static <M> BroadcastEvent<M> broadcast(ObjectEvent<M> origin) {
        BroadcastEvent<M> event;
        M source = process(origin.getSource());
        if (origin instanceof ObjectCreatedEvent) {
            event = new ObjectCreatedBroadcastEvent<M>(source);
        } else if (origin instanceof ObjectDestroyedEvent) {
            event = new ObjectDestroyedBroadcastEvent<M>(source);
        } else if (origin instanceof ObjectUpdatedEvent) {
            ObjectUpdatedEvent<M> updatedEvent = (ObjectUpdatedEvent<M>) origin;
            Object legacy = process(updatedEvent.getLegacy());
            event = new ObjectUpdatedBroadcastEvent<M>(source, legacy);
        } else {
            throw new UnsupportedOperationException("Can't convert " + origin + " as broadcast event now");
        }
        return event;
    }

    //为了防止直接将Mybatis Enhancer传入
    static <M> M process(M instance){
        Class<?> klass = instance.getClass();
        if(!isProxied(klass))
            return instance;
        try {
            Constructor<?> constructor = klass.getSuperclass().getConstructor();
            Method apply = klass.getSuperclass().getMethod("apply", Record.class, String[].class);
            //noinspection unchecked
            M unwrap = (M) constructor.newInstance();
            apply.invoke(unwrap, instance, new String[0]);
            return unwrap;
        }catch (Exception ex){
            return instance;
        }
    }

    static boolean isProxied(Class klass){
        return klass.getName().contains("$$EnhancerByCGLIB$$");
    }
}
