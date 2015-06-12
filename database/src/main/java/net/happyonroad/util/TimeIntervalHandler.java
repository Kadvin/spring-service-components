/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.type.TimeInterval;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Schedule Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class TimeIntervalHandler implements TypeHandler<TimeInterval> {
    @Override
    public void setParameter(PreparedStatement ps, int i, TimeInterval parameter, JdbcType jdbcType) throws SQLException {
        if( parameter == null )
            ps.setString(i, null);
        else{
            ps.setString(i, parameter.getInterval());
        }
    }

    @Override
    public TimeInterval getResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToTimeInterval(raw);
    }

    @Override
    public TimeInterval getResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToTimeInterval(raw);
    }

    @Override
    public TimeInterval getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToTimeInterval(raw);
    }

    private TimeInterval stringToTimeInterval(String raw) {
        if( raw != null ){
            //兼容JsonHandler的数据
            if( raw.contains(JsonHandler.SPLIT) ){
                raw = raw.split(JsonHandler.SPLIT)[0];
            }
            return new TimeInterval(raw);
        }
        return null;
    }
}
