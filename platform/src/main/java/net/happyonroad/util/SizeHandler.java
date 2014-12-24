/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.support.JsonSupport;
import net.happyonroad.type.Size;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Size Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class SizeHandler implements TypeHandler<Size> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Size parameter, JdbcType jdbcType) throws SQLException {
        if( parameter == null )
            ps.setString(i, null);
        else{
            ps.setString(i, JsonSupport.toJSONString(parameter));
        }
    }

    @Override
    public Size getResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToSize(raw);
    }

    @Override
    public Size getResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToSize(raw);
    }

    @Override
    public Size getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToSize(raw);
    }

    private Size stringToSize(String raw) {
        if( raw != null ){
            return JsonSupport.parseJson(raw, Size.class);
        }
        return null;
    }
}
