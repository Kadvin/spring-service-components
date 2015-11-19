/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.type.Schedule;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

/**
 * Schedule Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ScheduleHandler extends GenericJsonHandler<Schedule> {
    @Override
    protected Class<Schedule> objectClass() {
        return Schedule.class;
    }
}
