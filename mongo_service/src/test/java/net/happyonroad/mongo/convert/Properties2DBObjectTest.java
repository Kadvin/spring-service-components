package net.happyonroad.mongo.convert;

import org.junit.Test;

import static net.happyonroad.mongo.convert.Properties2DBObject.encodeKey;
import static org.junit.Assert.*;

public class Properties2DBObjectTest {

    @Test
    public void testEncodeKey() throws Exception {
        assertEquals("ab\\u002ecd\\\\12\\u002432", encodeKey("ab.cd\\12$32"));
    }
}