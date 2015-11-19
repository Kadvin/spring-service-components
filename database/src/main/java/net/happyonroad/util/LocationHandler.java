/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.type.Location;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

/**
 * Location Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class LocationHandler extends GenericJsonHandler<Location> {
    @Override
    protected Class<Location> objectClass() {
        return Location.class;
    }
}
