package net.happyonroad.support;

import net.happyonroad.model.GeneralMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class DefaultGeneralMapTest {
    GeneralMap map = new DefaultGeneralMap();

    @Test
    public void testInsensitive() throws Exception {
        map.put("HELLO", "world");
        assertEquals("world", map.get("hello"));
        assertEquals("world", map.get("Hello"));

        assertEquals("world", map.remove("HeLlo"));
        assertEquals(0, map.size());
    }

    @Test
    public void testInsensitiveOverride() throws Exception {
        map.put("HELLO", "world");
        assertEquals("world", map.get("hello"));
        map.put("Hello", "monitor");
        assertEquals(1, map.size());
        assertEquals("monitor", map.get("hello"));
    }
}