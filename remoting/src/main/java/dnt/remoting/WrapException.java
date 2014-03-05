/**
 * @author XiongJie, Date: 13-12-16
 */
package dnt.remoting;

import org.springframework.remoting.RemoteInvocationFailureException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/** A wrap exception */
public class WrapException extends RemoteInvocationFailureException {
    private static final long serialVersionUID = 2354078734280809913L;
    private String wrappedExceptionClass;
    //采用一个超级恶心的机制
    private int errorCode;

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

    @SuppressWarnings("UnusedDeclaration")
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        throw new UnsupportedOperationException("The wrap exception do not support init cause after constructor!");
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
}
