/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.type.Point;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

/**
 * Point Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class PointHandler extends GenericJsonHandler<Point> {
    @Override
    protected Class<Point> objectClass() {
        return Point.class;
    }
}
