/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.type.TimeSpan;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TimeSpan Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class TimeSpanHandler implements TypeHandler<TimeSpan> {
    @Override
    public void setParameter(PreparedStatement ps, int i, TimeSpan parameter, JdbcType jdbcType) throws SQLException {
        if( parameter == null )
            ps.setString(i, null);
        else{
            ps.setString(i, ParseUtils.toJSONString(parameter));
        }
    }

    @Override
    public TimeSpan getResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToTimeSpan(raw);
    }

    @Override
    public TimeSpan getResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToTimeSpan(raw);
    }

    @Override
    public TimeSpan getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToTimeSpan(raw);
    }

    private TimeSpan stringToTimeSpan(String raw) {
        if( raw != null ){
            //兼容JsonHandler的数据
            if( raw.contains(JsonHandler.SPLIT) ){
                raw = raw.split(JsonHandler.SPLIT)[0];
            }
            return ParseUtils.parseJson(raw, TimeSpan.class);
        }
        return null;
    }
}
