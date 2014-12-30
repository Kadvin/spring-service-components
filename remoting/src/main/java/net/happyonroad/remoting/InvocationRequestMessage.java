/**
 * @author XiongJie, Date: 13-11-1
 */
package net.happyonroad.remoting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import net.happyonroad.util.ParseUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.remoting.support.RemoteInvocation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** 发起调用的消息 */
@SuppressWarnings("UnusedDeclaration")
public class InvocationRequestMessage extends InvocationMessage {
    private static final long serialVersionUID = 7093541768647287416L;
    /** 请求的返回队列名 */
    private String                    replyTo;
    /* 调用的方法名称 */
    private String                    methodName;
    /* 调用的参数类型 */
    private String[]                  parameterTypes;
    /* 调用的参数 */
    private Object[]                  arguments;
    /* 额外的调用属性 */
    private Map<String, Serializable> attributes;

    public InvocationRequestMessage() {
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setParameterTypes(String[] parameterTypes) {

        this.parameterTypes = parameterTypes;
    }

    public String[] getParameterTypes() {
        return this.parameterTypes;
    }

    public void fillParameterClasses(Class[] parameterTypes) {

        this.parameterTypes = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            this.parameterTypes[i] = parameterTypes[i].getName();
            // Thread.currentThread().getContextClassLoader()
        }
    }

    public Class<?>[] fetchParameterClasses() throws ClassNotFoundException {
        return fetchParameterClasses(Thread.currentThread().getContextClassLoader());
    }

    public Class<?>[] fetchParameterClasses(ClassLoader cl) throws ClassNotFoundException {

        Class<?>[] classTypes = new Class<?>[this.parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; ++i) {
            classTypes[i] = Class.forName(parameterTypes[i], true, cl);
        }

        return classTypes;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public void addAttribute(String key, Serializable value) throws IllegalStateException {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Serializable>();
        }
        if (this.attributes.containsKey(key)) {
            throw new IllegalStateException("There is already an attribute with key '" + key + "' bound");
        }
        this.attributes.put(key, value);
    }

    public Serializable getAttribute(String key) {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.get(key);
    }

    @JsonDeserialize(contentUsing = UntypedObjectDeserializer.class)
    public void setAttributes(Map<String, Serializable> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Serializable> getAttributes() {
        return this.attributes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("replyTo", this.replyTo)
                .append("methodName", this.methodName)
                .toString();
    }

    public RemoteInvocation getInvocation(ClassLoader cl) throws ClassNotFoundException {
        return new RemoteInvocation(this.getMethodName(), this.fetchParameterClasses(cl), this.getArguments());
    }

    public RemoteInvocation asInvocation() throws ClassNotFoundException {

        return new RemoteInvocation(this.getMethodName(), this.fetchParameterClasses(), this.getArguments());
    }

    public String toJson() {
        return ParseUtils.toJSONString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvocationRequestMessage that = (InvocationRequestMessage) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(arguments, that.arguments)) return false;
        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) return false;
        if (!methodName.equals(that.methodName)) return false;
        if (!Arrays.equals(parameterTypes, that.parameterTypes)) return false;
        //noinspection RedundantIfStatement
        if (!replyTo.equals(that.replyTo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = replyTo.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + Arrays.hashCode(arguments);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    public static InvocationRequestMessage parse(String json) {
        return ParseUtils.parseJson(json, InvocationRequestMessage.class);
    }
}
