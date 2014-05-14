/**
 * @author XiongJie, Date: 13-11-20
 */
package dnt.support;

import dnt.model.Jsonable;
import net.minidev.json.JSONValue;

/** Description */
public class JsonSupport extends BinarySupport implements Jsonable {
    private static final long serialVersionUID = -5873792929187680622L;

    /**
     * Parse Json String to Target Object
     *
     * @param content  json string
     * @param theClass target object class
     * @param <T>      target type
     * @return target instance
     */
    public static <T> T parseJson(String content, Class<T> theClass) {
        T t = JSONValue.parse(content, theClass);
        if (t == null) {
            throw new IllegalArgumentException("Can't parse " + content + " to " + theClass.getSimpleName());
        }
        return t;
    }

    public String toJson() {
        return JSONValue.toJSONString(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
