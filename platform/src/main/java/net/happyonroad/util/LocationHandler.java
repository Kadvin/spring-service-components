/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.support.JsonSupport;
import net.happyonroad.type.Location;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Location Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class LocationHandler implements TypeHandler<Location> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Location parameter, JdbcType jdbcType) throws SQLException {
        if( parameter == null )
            ps.setString(i, null);
        else{
            ps.setString(i, JsonSupport.toJSONString(parameter));
        }
    }

    @Override
    public Location getResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToLocation(raw);
    }

    @Override
    public Location getResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToLocation(raw);
    }

    @Override
    public Location getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToLocation(raw);
    }

    private Location stringToLocation(String raw) {
        if( raw != null ){
            return JsonSupport.parseJson(raw, Location.class);
        }
        return null;
    }
}
