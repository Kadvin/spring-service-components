package net.happyonroad.credential;

import net.happyonroad.util.ParseUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class CredentialPropertiesTest {
    CredentialProperties credential = new CredentialProperties();

    @Test
    public void testToJson() throws Exception {
        credential.setName("jmx");
        credential.setType("jmx");
        credential.setProperty("enabled", "true");
        System.out.println(ParseUtils.toJSONString(credential));
    }
}