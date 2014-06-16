/**
 * @author XiongJie, Date: 13-11-15
 */
package dnt.remoting;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;

/** Test the json serialize */
public class InvocationRequestMessageTest {
    public static final String JSON =
            "{\"replyTo\":\"Queue/Replies\",\"methodName\":\"work\"," +
            "\"parameterTypes\":[\"java.lang.String\",\"java.lang.Integer\"],\"arguments\":[\"hello\",123]," +
            "\"attributes\":{\"hello\":\"world\"}}";
    private InvocationRequestMessage msg;

    @Before
    public void setUp() throws Exception {
        msg = new InvocationRequestMessage();
        msg.setMethodName("work");
        msg.setParameterTypes(new String[]{"java.lang.String", "java.lang.Integer"});
        msg.setArguments(new Object[]{"hello", 123});
        msg.setReplyTo("Queue/Replies");
        msg.setAttributes(new HashMap<String, Serializable>());
        msg.getAttributes().put("hello", "world");
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
    public void testToJson() throws Exception {
        String json = msg.toJson();
        System.out.println(json);
        Assert.assertEquals(JSON, json);
    }

    @Test
    public void testFromJson() throws Exception {
        InvocationRequestMessage another = InvocationRequestMessage.parse(JSON);
        Assert.assertEquals(msg, another);
    }

    @Test
    public void testForAnotherCase() throws Exception {
        String json = "{\"arguments\":[\"DNT\"],\"replyTo\":\"TestEngine\\/dnt.test.DemoRemoteService\\/Replies\\/74d14866-02de-40ed-8dbc-81d212d28ad8\",\"attributes\":null,\"methodName\":\"sayHello\",\"parameterTypes\":[\"java.lang.String\"]}";
        InvocationRequestMessage another = InvocationRequestMessage.parse(json);
        Assert.assertEquals("sayHello", another.getMethodName());
    }

    @Test
    public void testForReal() throws Exception {
        String json = "{\"arguments\":[\"b6c7dee8-ebb4-4f13-8b9c-d80996d7cb99\"],\"replyTo\":\"000-111-222-3333\\/dnt.dss.engine.ManagedObjectService\\/Replies\\/3e43f8c8-5d08-437c-81dc-b03c38c98098\",\"attributes\":null,\"methodName\":\"isAssigned\",\"parameterTypes\":[\"java.lang.String\"]}";
        InvocationRequestMessage real = InvocationRequestMessage.parse(json);
        Assert.assertEquals("isAssigned", real.getMethodName());
    }

    @Test
    @Ignore
    public void testWithMo() throws Exception{
        String json = "{\"arguments\":[{\"accesses\":{\"snmp\":{\"community\":\"public\",\"version\":\"v2c\"},\"ssh\":{\"user\":\"root\",\"password\":\"private\",\"timeout\":500}},\"address\":{\"port\":1008,\"host\":\"192.168.0.10\"},\"name\":\"Center Router\",\"attributes\":{\"test\":\"attribute\"},\"type\":\"NetworkDevice\",\"key\":\"b6c7dee8-ebb4-4f13-8b9c-d80996d7cb99\"}],\"replyTo\":\"000-111-222-3333\\/dnt.dss.engine.ManagedObjectService\\/Replies\\/4f40dbd1-b4f0-4c75-9f5b-b05f399ef1ca\",\"attributes\":null,\"methodName\":\"assign\",\"parameterTypes\":[\"dnt.dss.model.ManagedObject\"]}";
        InvocationRequestMessage real = InvocationRequestMessage.parse(json);
        Assert.assertEquals("assign", real.getMethodName());

    }
}
