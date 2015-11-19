/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.type.TimeSpan;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

/**
 * TimeSpan Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class TimeSpanHandler extends GenericJsonHandler<TimeSpan> {
    @Override
    protected Class<TimeSpan> objectClass() {
        return TimeSpan.class;
    }
}
