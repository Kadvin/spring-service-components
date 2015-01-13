/**
 * Developer: Kadvin Date: 14-6-16 下午1:50
 */
package net.happyonroad.access;

import net.happyonroad.credential.SnmpCredential;
import net.happyonroad.credential.SnmpPassport;
import net.happyonroad.util.ParseUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试 Snmp Credential to/from Json
 */
public class SnmpCredentialTest {
    public static final String JSON =
            "{\"class\":\"net.happyonroad.credential.SnmpCredential\",\"version\":\"v3\",\"community\":\"public\",\"port\":161,\"timeout\":100,\"retries\":3,\"passport\":{\"authenticateMethod\":\"MD5\",\"encryptMethod\":\"DES\",\"privateKey\":null,\"userName\":\"admin\",\"password\":\"secret\",\"context\":null}}";
    private static SnmpCredential SNMP;

    static {
        SnmpPassport passport = new SnmpPassport();
        passport.setUserName("admin");
        passport.setPassword("secret");
        SNMP = new SnmpCredential();
        SNMP.setVersion("v3");
        SNMP.setTimeout(100);
        SNMP.setPort(161);
        SNMP.setRetries(3);
        SNMP.setPassport(passport);
    }

    @Test
    public void testSerializeJson() throws Exception {
        String json = ParseUtils.toJSONString(SNMP);
        System.out.println(json);
        Assert.assertEquals(JSON, json);
    }

    @Test
    public void testDeserializeJson() throws Exception {
        SnmpCredential parsed = ParseUtils.parseJson(JSON, SnmpCredential.class);
        Assert.assertEquals(SNMP, parsed);
    }
}
