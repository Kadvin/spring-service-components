/**
 * Developer: Kadvin Date: 14-6-16 下午1:46
 */
package dnt.access;

import org.junit.Assert;
import org.junit.Test;

/**
 * 测试 Ssh Access to/from Json
 */
public class SshAccessTest {
    public static final String JSON = "{\"user\":\"root\",\"password\":\"secret\",\"timeout\":100}";
    SshAccess SSH = new SshAccess("root", "secret", 100);

    @Test
    public void testSerializeJson() throws Exception {
        String json = SSH.toJson();
        System.out.println(json);
        Assert.assertEquals(JSON, json);
    }

    @Test
    public void testDeserializeJson() throws Exception {
        SshAccess parsed = SshAccess.parseJson(JSON, SshAccess.class);
        Assert.assertEquals(SSH, parsed);
    }


}
