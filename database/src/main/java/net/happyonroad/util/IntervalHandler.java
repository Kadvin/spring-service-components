/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.Interval;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interval Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class IntervalHandler implements TypeHandler<Interval> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Interval parameter, JdbcType jdbcType) throws SQLException {
        if( parameter == null )
            ps.setString(i, null);
        else{
            ps.setString(i, ParseUtils.toJSONString(parameter));
        }
    }

    @Override
    public Interval getResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToValue(raw);
    }

    @Override
    public Interval getResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToValue(raw);
    }

    @Override
    public Interval getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToValue(raw);
    }

    private Interval stringToValue(String raw) {
        if( raw != null ){
            //兼容JsonHandler的数据
            if( raw.contains(JsonHandler.SPLIT) ){
                raw = raw.split(JsonHandler.SPLIT)[0];
            }
            return ParseUtils.parseJson(raw, Interval.class);
        }
        return null;
    }
}
