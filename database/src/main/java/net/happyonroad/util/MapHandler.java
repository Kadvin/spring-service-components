package net.happyonroad.util;

import java.util.Map;

/**
 * <h1>Map Handler</h1>
 *
 * @author Jay Xiong
 */
public class MapHandler extends GenericJsonHandler<Map> {
    @Override
    protected Class<Map> objectClass() {
        return Map.class;
    }
}
