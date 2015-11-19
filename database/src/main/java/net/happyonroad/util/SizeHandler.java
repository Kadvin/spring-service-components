/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.type.Size;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

/**
 * Size Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class SizeHandler extends GenericJsonHandler<Size> {
    @Override
    protected Class<Size> objectClass() {
        return Size.class;
    }
}
