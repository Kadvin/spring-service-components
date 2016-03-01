package net.happyonroad.mongo.convert;

import org.junit.Test;

import static net.happyonroad.mongo.convert.DBObject2Properties.decodeKey;
import static org.junit.Assert.*;

public class DBObject2PropertiesTest {

    @Test
    public void testDecodeKey() throws Exception {
        assertEquals("ab.cd\\12$32", decodeKey("ab\\u002ecd\\\\12\\u002432"));
    }
}