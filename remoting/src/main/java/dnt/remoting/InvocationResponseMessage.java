/**
 * @author XiongJie, Date: 13-11-1
 */
package dnt.remoting;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dnt.support.JsonSupport;

/** 调用返回的消息 */
public class InvocationResponseMessage extends InvocationMessage {
    private static final long serialVersionUID = -491976407273382425L;
    private Object    value;
    private WrapException error;

    public void setValue(Object value) {
        this.value = value;
    }

    @JsonDeserialize(as = WrapException.class)
    public void setError(Throwable error) {
        if( error != null )
        {
            if( error instanceof WrapException){
                this.error = (WrapException) error;
            }else{
                this.error = new WrapException(error);
            }
        }
        else this.error = null;
    }

    public String toJson() {
        return JsonSupport.toJSONString(this);
    }

    public static InvocationResponseMessage parse(String msg) {
        return JsonSupport.parseJson(msg, InvocationResponseMessage.class);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvocationResponseMessage)) return false;

        InvocationResponseMessage that = (InvocationResponseMessage) o;

        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }
}
