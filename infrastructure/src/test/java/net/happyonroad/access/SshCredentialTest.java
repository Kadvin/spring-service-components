/**
 * Developer: Kadvin Date: 14-6-16 下午1:46
 */
package net.happyonroad.access;

import net.happyonroad.credential.SshCredential;
import org.junit.Assert;
import org.junit.Test;

/**
 * 测试 Ssh Credential to/from Json
 */
public class SshCredentialTest {
    public static final String JSON = "{\"user\":\"root\",\"password\":\"secret\",\"timeout\":100}";
    SshCredential SSH = new SshCredential("root", "secret", 100);

    @Test
    public void testSerializeJson() throws Exception {
        String json = SSH.toJson();
        System.out.println(json);
        Assert.assertEquals(JSON, json);
    }

    @Test
    public void testDeserializeJson() throws Exception {
        SshCredential parsed = SshCredential.parseJson(JSON, SshCredential.class);
        Assert.assertEquals(SSH, parsed);
    }


}
