/**
 * @author XiongJie, Date: 13-11-1
 */
package net.happyonroad.remoting;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.happyonroad.util.ParseUtils;

/** 调用返回的消息 */
public class InvocationResponseMessage extends InvocationMessage {
    private static final long serialVersionUID = -491976407273382425L;
    private ClassAndValue pair;
    private WrapException error;

    @JsonIgnore
    public Object getValue() {
        return pair == null ? null : pair.value;
    }

    @JsonIgnore
    public void setValue(Object value) {
        pair = new ClassAndValue();
        pair.klass = value.getClass();
        pair.value = value;
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonSerialize(using = ClassAndValueSerializer.class)
    public ClassAndValue getPair() {
        return pair;
    }
    @SuppressWarnings("UnusedDeclaration")
    @JsonDeserialize(using = ClassAndValueDeserializer.class)
    public void setPair(ClassAndValue pair) {
        this.pair = pair;
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
        return ParseUtils.toJSONString(this);
    }

    public static InvocationResponseMessage parse(String msg) {
        return ParseUtils.parseJson(msg, InvocationResponseMessage.class);
    }

    public Throwable getError() {
        return error;
    }

    public Throwable recreateError() {
        return error.recreate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvocationResponseMessage)) return false;

        InvocationResponseMessage that = (InvocationResponseMessage) o;

        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        //noinspection RedundantIfStatement
        if (pair != null ? !pair.equals(that.pair) : that.pair != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pair != null ? pair.hashCode() : 0;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }
}
