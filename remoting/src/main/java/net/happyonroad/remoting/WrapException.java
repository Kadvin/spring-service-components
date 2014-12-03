/**
 * @author XiongJie, Date: 13-12-16
 */
package net.happyonroad.remoting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.remoting.RemoteInvocationFailureException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/** A wrap exception */
@SuppressWarnings("UnusedDeclaration")
public class WrapException extends RemoteInvocationFailureException {
    private static final long serialVersionUID = 2354078734280809913L;
    private String wrappedExceptionClass;
    //采用一个超级恶心的机制
    private int errorCode;


    @JsonCreator
    public WrapException(Map properties){
        super((String) properties.get("message"), null);
        this.wrappedExceptionClass = (String) properties.get("wrappedExceptionClass");
        Object code = properties.get("errorCode");
        if( code != null ) this.errorCode = Integer.valueOf(code.toString());
        if(properties.get("stackTrace") != null ){
            //noinspection unchecked
            List<Map> stack = (List<Map>) properties.get("stackTrace");
            StackTraceElement[] elements = new StackTraceElement[stack.size()];
            for (int i = 0; i < stack.size(); i++) {
                Map map = stack.get(i);
                elements[i] = new StackTraceElement((String)map.get("className"),
                                                    (String)map.get("methodName"),
                                                    (String)map.get("fileName"),
                                                    (Integer)map.get("lineNumber"));
            }
            setStackTrace(elements);
        }
    }

    public WrapException(Throwable wrapped) {
        super(wrapped.getMessage(), null);
        this.wrappedExceptionClass = wrapped.getClass().getName();
        setStackTrace(wrapped.getStackTrace());
        try {
            Method getErrorCode = wrapped.getClass().getMethod("getErrorCode");
            if(getErrorCode != null ){
                this.errorCode = (Integer)getErrorCode.invoke(wrapped);
            }
        }catch (Exception ex){
            //skip
        }
        if (wrapped.getCause() != null && wrapped.getCause() != wrapped) {
            super.initCause(new WrapException(wrapped.getCause()));
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getWrappedExceptionClass() {
        return wrappedExceptionClass;
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        throw new UnsupportedOperationException("The wrap exception do not support init cause after constructor!");
    }

    @JsonIgnore
    @Override
    public Throwable getMostSpecificCause() {
        return super.getMostSpecificCause();
    }

    @JsonIgnore
    @Override
    public Throwable getRootCause() {
        return super.getRootCause();
    }

    @JsonIgnore
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @JsonIgnore
    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }

    @Override
    public String toString() {
        String s = wrappedExceptionClass;
        String message = getLocalizedMessage();
        if(errorCode != 0 ) s = s + "(" + errorCode + ")";
        return (message != null) ? (s + ": " + message ) : s;
    }

    public Throwable recreate() {
        try{
            Class<?> originExceptionClass = Class.forName(this.wrappedExceptionClass);
            Throwable origin;
            Constructor<?> constructor;
            try {
                constructor = originExceptionClass.getConstructor(int.class, String.class);
                origin = (Throwable) constructor.newInstance(errorCode, this.getMessage());
            } catch (NoSuchMethodException e) {
                constructor = originExceptionClass.getConstructor(String.class);
                origin = (Throwable) constructor.newInstance(this.getMessage());
            }

            if(this.getCause() != null ){
                WrapException cause = (WrapException) this.getCause();
                origin.initCause(cause.recreate());
            }
            return origin;
        }catch (Exception cfe){
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WrapException)) return false;

        WrapException that = (WrapException) o;

        if (errorCode != that.errorCode) return false;
        if (!wrappedExceptionClass.equals(that.wrappedExceptionClass)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = wrappedExceptionClass.hashCode();
        result = 31 * result + errorCode;
        return result;
    }
}
