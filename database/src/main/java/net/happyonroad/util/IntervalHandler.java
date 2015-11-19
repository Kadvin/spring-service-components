/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.joda.time.Interval;

/**
 * Interval Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class IntervalHandler extends GenericJsonHandler<Interval> {
    @Override
    protected Class<Interval> objectClass() {
        return Interval.class;
    }
}
