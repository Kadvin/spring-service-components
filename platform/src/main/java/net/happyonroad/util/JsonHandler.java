/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.IllegalFormatException;

/**
 * Location Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonHandler implements TypeHandler<Object> {
    public static final String SPLIT = "@@->";

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if( parameter == null )
            ps.setString(i, null);
        else{
            ps.setString(i, objectToString(parameter));
        }
    }

    @Override
    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToObject(raw);
    }

    @Override
    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToObject(raw);
    }

    @Override
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToObject(raw);
    }

    private Object stringToObject(String raw) {
        if( raw != null ){
            String[] data = raw.split(SPLIT);
            if( data.length != 2)
                throw new IllegalArgumentException("Invalid json:" + raw);
            try {
                Class<?> klass = Resources.classForName(data[1]);
                return ParseUtils.parseJson(data[0], klass);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Can't find class " + data[1]);
            }
        }
        return null;
    }

    private String objectToString(Object param){
        return ParseUtils.toJSONString(param) + SPLIT + param.getClass().getName();
    }
}
