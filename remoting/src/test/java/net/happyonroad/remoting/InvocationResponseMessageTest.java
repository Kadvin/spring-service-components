/**
 * Developer: Kadvin Date: 14-6-16 下午3:58
 */
package net.happyonroad.remoting;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试调用返回的消息
 */
public class InvocationResponseMessageTest {
    public static final String NORMAL_JSON  =
            "{\"pair\":{\"klass\":\"java.lang.String\",\"value\":\"normal value\"},\"error\":null}";
    public static final String ERROR_JSON_1 = "{\"pair\":null";
    public static final String ERROR_JSON_2 = "\"errorCode\":0,\"message\":\"A demo runtime exception\"";
    private InvocationResponseMessage normalMsg, errorMsg;

    @Before
    public void setUp() throws Exception {
        normalMsg = new InvocationResponseMessage();
        normalMsg.setValue("normal value");
        errorMsg = new InvocationResponseMessage();
        errorMsg.setError(new RuntimeException("A demo runtime exception"));
    }

    /**
     * <dl>
     * <dt>测试目的:</dt>
     * <dd>测试该对象可以通过JSON序列化，反序列化</dd>
     * <dt>验证手段:</dt>
     * <dd>序列化可以成功</dd>
     * <dd>反序列化之后的对象与原对象等同</dd>
     * </dl>
     *
     * @throws Exception Any Exception
     */
    @Test
    public void testToJsonForNormalMessage() throws Exception {
        String json = normalMsg.toJson();
        System.out.println(json);
        Assert.assertEquals(NORMAL_JSON, json);
    }

    @Test
    public void testFromJsonOfNormalMessage() throws Exception {
        InvocationResponseMessage another = InvocationResponseMessage.parse(NORMAL_JSON);
        Assert.assertEquals(normalMsg, another);
    }

    @Test
    public void testToJsonForErrorMessage() throws Exception {
        String json = errorMsg.toJson();
        System.out.println(json);
        Assert.assertTrue(json.contains(ERROR_JSON_1));
        Assert.assertTrue(json.contains(ERROR_JSON_2));
    }

    @Test
    public void testFromJsonOfErrorMessage() throws Exception {
        String ERROR_JSON = "{\"pair\":null,\"error\":{\"stackTrace\":[{\"methodName\":\"setUp\",\"fileName\":\"InvocationResponseMessageTest.java\",\"lineNumber\":24,\"className\":\"InvocationResponseMessageTest\",\"nativeMethod\":false},{\"methodName\":\"invoke0\",\"fileName\":\"NativeMethodAccessorImpl.java\",\"lineNumber\":-2,\"className\":\"sun.reflect.NativeMethodAccessorImpl\",\"nativeMethod\":true},{\"methodName\":\"invoke\",\"fileName\":\"NativeMethodAccessorImpl.java\",\"lineNumber\":57,\"className\":\"sun.reflect.NativeMethodAccessorImpl\",\"nativeMethod\":false},{\"methodName\":\"invoke\",\"fileName\":\"DelegatingMethodAccessorImpl.java\",\"lineNumber\":43,\"className\":\"sun.reflect.DelegatingMethodAccessorImpl\",\"nativeMethod\":false},{\"methodName\":\"invoke\",\"fileName\":\"Method.java\",\"lineNumber\":601,\"className\":\"java.lang.reflect.Method\",\"nativeMethod\":false},{\"methodName\":\"runReflectiveCall\",\"fileName\":\"FrameworkMethod.java\",\"lineNumber\":44,\"className\":\"org.junit.runners.model.FrameworkMethod$1\",\"nativeMethod\":false},{\"methodName\":\"run\",\"fileName\":\"ReflectiveCallable.java\",\"lineNumber\":15,\"className\":\"org.junit.internal.runners.model.ReflectiveCallable\",\"nativeMethod\":false},{\"methodName\":\"invokeExplosively\",\"fileName\":\"FrameworkMethod.java\",\"lineNumber\":41,\"className\":\"org.junit.runners.model.FrameworkMethod\",\"nativeMethod\":false},{\"methodName\":\"evaluate\",\"fileName\":\"RunBefores.java\",\"lineNumber\":27,\"className\":\"org.junit.internal.runners.statements.RunBefores\",\"nativeMethod\":false},{\"methodName\":\"runNotIgnored\",\"fileName\":\"BlockJUnit4ClassRunner.java\",\"lineNumber\":79,\"className\":\"org.junit.runners.BlockJUnit4ClassRunner\",\"nativeMethod\":false},{\"methodName\":\"runChild\",\"fileName\":\"BlockJUnit4ClassRunner.java\",\"lineNumber\":71,\"className\":\"org.junit.runners.BlockJUnit4ClassRunner\",\"nativeMethod\":false},{\"methodName\":\"runChild\",\"fileName\":\"BlockJUnit4ClassRunner.java\",\"lineNumber\":49,\"className\":\"org.junit.runners.BlockJUnit4ClassRunner\",\"nativeMethod\":false},{\"methodName\":\"run\",\"fileName\":\"ParentRunner.java\",\"lineNumber\":193,\"className\":\"org.junit.runners.ParentRunner$3\",\"nativeMethod\":false},{\"methodName\":\"schedule\",\"fileName\":\"ParentRunner.java\",\"lineNumber\":52,\"className\":\"org.junit.runners.ParentRunner$1\",\"nativeMethod\":false},{\"methodName\":\"runChildren\",\"fileName\":\"ParentRunner.java\",\"lineNumber\":191,\"className\":\"org.junit.runners.ParentRunner\",\"nativeMethod\":false},{\"methodName\":\"access$000\",\"fileName\":\"ParentRunner.java\",\"lineNumber\":42,\"className\":\"org.junit.runners.ParentRunner\",\"nativeMethod\":false},{\"methodName\":\"evaluate\",\"fileName\":\"ParentRunner.java\",\"lineNumber\":184,\"className\":\"org.junit.runners.ParentRunner$2\",\"nativeMethod\":false},{\"methodName\":\"run\",\"fileName\":\"ParentRunner.java\",\"lineNumber\":236,\"className\":\"org.junit.runners.ParentRunner\",\"nativeMethod\":false},{\"methodName\":\"run\",\"fileName\":\"JUnitCore.java\",\"lineNumber\":157,\"className\":\"org.junit.runner.JUnitCore\",\"nativeMethod\":false},{\"methodName\":\"startRunnerWithArgs\",\"fileName\":\"JUnit4IdeaTestRunner.java\",\"lineNumber\":74,\"className\":\"com.intellij.junit4.JUnit4IdeaTestRunner\",\"nativeMethod\":false},{\"methodName\":\"prepareStreamsAndStart\",\"fileName\":\"JUnitStarter.java\",\"lineNumber\":202,\"className\":\"com.intellij.rt.execution.junit.JUnitStarter\",\"nativeMethod\":false},{\"methodName\":\"main\",\"fileName\":\"JUnitStarter.java\",\"lineNumber\":65,\"className\":\"com.intellij.rt.execution.junit.JUnitStarter\",\"nativeMethod\":false}],\"errorCode\":0,\"wrappedExceptionClass\":\"java.lang.RuntimeException\",\"message\":\"A demo runtime exception\",\"suppressed\":[]}}";
        InvocationResponseMessage another = InvocationResponseMessage.parse(ERROR_JSON);
        Assert.assertEquals(errorMsg, another);
    }
}
