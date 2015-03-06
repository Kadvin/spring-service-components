/**
 * Developer: Kadvin Date: 15/3/5 下午5:47
 */
package net.happyonroad.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <h1>Generic Json Handler</h1>
 */
public abstract class GenericJsonHandler<T> extends BaseTypeHandler<T> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, objectToString(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToObject(raw);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToObject(raw);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToObject(raw);
    }

    protected String objectToString(T entry){
        return ParseUtils.toJSONString(entry);
    }

    protected T stringToObject(String content) {
        if( content == null ) return null;
        return ParseUtils.parseJson(content, objectClass());
    }


    protected abstract Class<T> objectClass();
}

