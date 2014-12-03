/**
 * Developer: Kadvin Date: 14-6-16 下午1:50
 */
package net.happyonroad.access;

import net.happyonroad.credential.SnmpCredential;
import net.happyonroad.credential.SnmpPassport;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试 Snmp Credential to/from Json
 */
public class SnmpCredentialTest {
    public static final String JSON =
            "{\"timeout\":100,\"retries\":3,\"port\":161,\"read\":{\"version\":\"v2c\",\"community\":\"public\",\"securityName\":null,\"authPassword\":null,\"privatePassword\":null},\"write\":{\"version\":\"v2c\",\"community\":\"public\",\"securityName\":null,\"authPassword\":null,\"privatePassword\":null}}";
    private static SnmpCredential SNMP;

    static {
        SnmpPassport readPassport = new SnmpPassport();
        readPassport.setCommunity("public");
        readPassport.setVersion("v2c");
        SNMP = new SnmpCredential();
        SNMP.setTimeout(100);
        SNMP.setPort(161);
        SNMP.setRetries(3);
        SNMP.setRead(readPassport);
    }

    @Test
    public void testSerializeJson() throws Exception {
        String json = SNMP.toJson();
        System.out.println(json);
        Assert.assertEquals(JSON, json);
    }

    @Test
    public void testDeserializeJson() throws Exception {
        SnmpCredential parsed = SnmpCredential.parseJson(JSON, SnmpCredential.class);
        Assert.assertEquals(SNMP, parsed);
    }
}
