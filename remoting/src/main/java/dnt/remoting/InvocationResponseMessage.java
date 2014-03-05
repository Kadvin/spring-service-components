/**
 * @author XiongJie, Date: 13-11-1
 */
package dnt.remoting;

import net.minidev.json.JSONValue;

/** 调用返回的消息 */
public class InvocationResponseMessage extends InvocationMessage {
    private static final long serialVersionUID = -491976407273382425L;
    private Object    value;
    private WrapException error;

    public void setValue(Object value) {
        this.value = value;
    }

    public void setError(Throwable error) {
        this.error = new WrapException(error);
    }

    public String toJson() {
        return JSONValue.toJSONString(this);
    }

    public static InvocationResponseMessage parse(String msg) {
        return JSONValue.parse(msg, InvocationResponseMessage.class);
    }

    public Throwable getError() {
        return error;
    }

    public Throwable recreateError() {
        return error.recreate();
    }

    public Object getValue() {
        return value;
    }
}
