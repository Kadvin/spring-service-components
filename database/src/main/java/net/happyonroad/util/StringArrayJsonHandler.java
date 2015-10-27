package net.happyonroad.util;

/**
 * <h1>String Array Json Handler</h1>
 *
 * @author Jay Xiong
 */
public class StringArrayJsonHandler extends GenericJsonHandler<String[]> {
    @Override
    protected Class<String[]> objectClass() {
        return String[].class;
    }
}
