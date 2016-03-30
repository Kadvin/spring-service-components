/**
 * @author XiongJie, Date: 13-11-1
 */
package net.happyonroad.remoting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import net.happyonroad.util.ParseUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 发起调用的消息
 */
public class InvocationRequestMessage extends InvocationMessage {
    private static final long serialVersionUID = 7093541768647287416L;
    private String                    serviceName;
    /* 调用的方法名称 */
    private String                    methodName;//方法的参数信息被合入arguments里面
    private ClassAndValue[]           arguments;// pure arguments,可能包含null，会丢失类型信息
    /* 额外的调用属性 */
    private Map<String, Serializable> attributes;

    public InvocationRequestMessage() {
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void populateArguments(Class[] argTypes, Object[] arguments) {
        if (this.arguments == null) {
            this.arguments = new ClassAndValue[argTypes.length];
        }
        for (int i = 0; i < argTypes.length; i++) {
            Class argType = argTypes[i];
            ClassAndValue pair = new ClassAndValue();
            pair.klass = argType;
            pair.value = arguments[i];
            this.arguments[i] = pair;
        }
    }

    @JsonSerialize(contentUsing = ClassAndValueSerializer.class)
    public ClassAndValue[] getArguments() {
        return arguments;
    }

    @JsonDeserialize(contentUsing = ClassAndValueDeserializer.class)
    public void setArguments(ClassAndValue[] arguments) {
        this.arguments = arguments;
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
        StringBuilder strArgs = new StringBuilder();
        for (ClassAndValue argument : arguments) {
            Class klass = argument.getKlass();
            if (klass == String.class) {
                strArgs.append("\"").append(argument.value).append("\"");
            } else {
                if (klass.isArray()) {
                    if(klass.getComponentType() == int.class){
                        strArgs.append(Arrays.toString((int[]) argument.value));
                    }else if (klass.getComponentType() == short.class){
                        strArgs.append(Arrays.toString((short[]) argument.value));
                    }else if (klass.getComponentType() == byte.class){
                        strArgs.append(Arrays.toString((byte[]) argument.value));
                    }else if (klass.getComponentType() == char.class){
                        strArgs.append(Arrays.toString((char[]) argument.value));
                    }else if (klass.getComponentType() == boolean.class){
                        strArgs.append(Arrays.toString((boolean[]) argument.value));
                    }else if (klass.getComponentType() == long.class){
                        strArgs.append(Arrays.toString((long[]) argument.value));
                    }else if (klass.getComponentType() == float.class){
                        strArgs.append(Arrays.toString((float[]) argument.value));
                    }else if (klass.getComponentType() == double.class){
                        strArgs.append(Arrays.toString((double[]) argument.value));
                    }else{
                        strArgs.append(Arrays.toString((Object[]) argument.value));
                    }
                } else {
                    strArgs.append(argument.value);
                }
            }
            strArgs.append(",");
        }
        if (strArgs.length() > 0) strArgs.deleteCharAt(strArgs.length() - 1);
        return String.format("%s#%s(%s)", ClassUtils.getShortName(serviceName), methodName, strArgs);
    }

    public RemoteInvocation asInvocation() {

        return new RemoteInvocation(this.getMethodName(), this.fetchArgumentTypes(), this.fetchArgumentValues());
    }

    private Class<?>[] fetchArgumentTypes() {
        Class<?>[] argTypes = new Class[this.arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            ClassAndValue pair = arguments[i];
            argTypes[i] = pair.klass;
        }
        return argTypes;
    }

    private Object[] fetchArgumentValues() {
        Object[] argValues = new Object[this.arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            ClassAndValue pair = arguments[i];
            argValues[i] = pair.value;
        }
        return argValues;
    }


    public String toJson() {
        return ParseUtils.toJSONString(this);
    }

    public static InvocationRequestMessage parse(String json) {
        return ParseUtils.parseJson(json, InvocationRequestMessage.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvocationRequestMessage)) return false;

        InvocationRequestMessage that = (InvocationRequestMessage) o;

        if (!Arrays.equals(arguments, that.arguments)) return false;
        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) return false;
        if (!methodName.equals(that.methodName)) return false;
        if (!serviceName.equals(that.serviceName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serviceName.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + (arguments != null ? Arrays.hashCode(arguments) : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }
}
