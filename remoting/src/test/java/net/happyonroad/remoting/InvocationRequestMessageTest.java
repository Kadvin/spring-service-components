/**
 * @author XiongJie, Date: 13-11-15
 */
package net.happyonroad.remoting;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;

/** Test the json serialize */
public class InvocationRequestMessageTest {
    public static final String JSON =
            "{\"serviceName\":\"java.lang.Object\",\"methodName\":\"work\"," +
            "\"arguments\":[{\"klass\":\"java.lang.String\",\"value\":\"hello\"},{\"klass\":\"java.lang.Integer\",\"value\":123}]," +
            "\"attributes\":{\"hello\":\"world\"}}";
    private InvocationRequestMessage msg;

    @Before
    public void setUp() throws Exception {
        msg = new InvocationRequestMessage();
        msg.setServiceName(Object.class.getName());
        msg.setMethodName("work");
        msg.populateArguments(new Class[]{String.class, Integer.class}, new Object[]{"hello", 123});
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
    @Ignore
    public void testWithMo() throws Exception{
        String json = "{\"arguments\":[{\"klass\":\"dnt.monitor.model.ManagedObject\",\"value\":{\"accesses\":{\"snmp\":{\"community\":\"public\",\"version\":\"v2c\"},\"ssh\":{\"user\":\"root\",\"password\":\"private\",\"timeout\":500}},\"address\":{\"port\":1008,\"host\":\"192.168.0.10\"},\"name\":\"Center Router\",\"attributes\":{\"test\":\"attribute\"},\"type\":\"NetworkDevice\",\"key\":\"b6c7dee8-ebb4-4f13-8b9c-d80996d7cb99\"}}],\"serviceName\":\"dnt.dss.engine.ManagedObjectService\",\"attributes\":null,\"methodName\":\"assign\"]}";
        InvocationRequestMessage real = InvocationRequestMessage.parse(json);
        Assert.assertEquals("assign", real.getMethodName());

    }
}
