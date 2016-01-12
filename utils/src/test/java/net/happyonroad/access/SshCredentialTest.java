/**
 * Developer: Kadvin Date: 14-6-16 下午1:46
 */
package net.happyonroad.access;

import net.happyonroad.credential.SshCredential;
import net.happyonroad.util.ParseUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试 Ssh Credential to/from Json
 */
public class SshCredentialTest {
    public static final String        JSON =
            "{\"@class\":\"net.happyonroad.credential.SshCredential\",\"name\":\"ssh\",\"type\":\"ssh\",\"enabled\":true,\"authenticateMethod\":\"password\",\"user\":\"root\",\"password\":\"secret\",\"permFile\":null,\"privateKey\":null,\"port\":22,\"timeout\":\"30s\"}";
    static              SshCredential SSH  = new SshCredential("root", "secret");

    static {
        SSH.setName("ssh");
    }

    @Test
    public void testSerializeJson() throws Exception {
        String json = ParseUtils.toJSONString(SSH);
        System.out.println(json);
        Assert.assertEquals(JSON, json);
    }

    @Test
    public void testDeserializeJson() throws Exception {
        SshCredential parsed = ParseUtils.parseJson(JSON, SshCredential.class);
        Assert.assertEquals(SSH, parsed);
    }


}
