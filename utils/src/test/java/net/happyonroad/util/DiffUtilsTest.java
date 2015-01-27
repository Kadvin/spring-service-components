package net.happyonroad.util;

import net.happyonroad.credential.SnmpCredential;
import net.happyonroad.credential.SnmpPassport;
import org.junit.Test;

public class DiffUtilsTest {

    @Test
    public void testDifference() throws Exception {
        SnmpCredential one = new SnmpCredential();
        one.setCommunity("public");
        one.setPort(123);
        one.setRetries(23);
        one.setTimeout(2032);
        one.setVersion("v2c");

        SnmpPassport onePassport = new SnmpPassport();
        one.setPassport(onePassport);
        onePassport.setAuthenticateMethod("SHA");
        onePassport.setEncryptMethod("AES");
        onePassport.setUserName("Admin");
        onePassport.setPassword("secret");


        SnmpCredential two = new SnmpCredential();
        two.setCommunity("private");
        two.setPort(1234);
        two.setRetries(234);
        two.setTimeout(2034);
        two.setVersion("v2c");

        SnmpPassport twoPassport = new SnmpPassport();
        two.setPassport(twoPassport);
        twoPassport.setAuthenticateMethod("MD5");
        twoPassport.setEncryptMethod("DES");
        twoPassport.setUserName("Admin");
        twoPassport.setPassword("secret");

        System.out.println(DiffUtils.describeDiff(one, two));
    }
}