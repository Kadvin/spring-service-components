/**
 * Developer: Kadvin Date: 14-9-16 上午9:50
 */
package net.happyonroad.util;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 负责序列化 Properties
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class PropertiesHandler implements TypeHandler<Properties> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Properties parameter, JdbcType jdbcType) throws SQLException {
        if( parameter == null )
            ps.setString(i, null);
        else{
            ps.setString(i, ParseUtils.toJSONString(parameter));
        }
    }

    @Override
    public Properties getResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToProperties(raw);
    }

    @Override
    public Properties getResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToProperties(raw);
    }

    @Override
    public Properties getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToProperties(raw);
    }

    protected Properties stringToProperties(String raw) throws SQLException {
        if( raw != null ){
            //兼容JsonHandler的数据
            if( raw.contains(JsonHandler.SPLIT) ){
                raw = raw.split(JsonHandler.SPLIT)[0];
            }
            return ParseUtils.parseJson(raw, Properties.class);
        }
        return null;
    }
}
