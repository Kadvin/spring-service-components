package net.happyonroad.util;

/**
 * <h1>Int Array Json Handler</h1>
 *
 * @author Jay Xiong
 */
public class IntArrayJsonHandler extends GenericJsonHandler<int[]> {
    @Override
    protected Class<int[]> objectClass() {
        return int[].class;
    }
}
