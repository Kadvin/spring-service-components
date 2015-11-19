package net.happyonroad.util;

import java.util.Map;

/**
 * <h1>A generic map array</h1>
 *
 * @author Jay Xiong
 */
public class MapArrayHandler extends GenericJsonHandler<Map[]> {
    @Override
    protected Class<Map[]> objectClass() {
        return Map[].class;
    }
}
