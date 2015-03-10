/**
 * Developer: Kadvin Date: 15/3/10 上午11:22
 */
package net.happyonroad.util;

import net.happyonroad.model.IpRange;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <h1>Mybatis Handler for IpRange</h1>
 */
public class IpRangeHandler extends BaseTypeHandler<IpRange> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, IpRange parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, objectToString(parameter));
    }

    @Override
    public IpRange getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToObject(raw);
    }

    @Override
    public IpRange getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToObject(raw);
    }

    @Override
    public IpRange getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToObject(raw);
    }

    protected String objectToString(IpRange entry){
        return entry.toString();
    }

    protected IpRange stringToObject(String content) {
        if( content == null ) return null;
        IpRange[] ranges = IpRange.parse(content);
        return ranges[0];
    }
}
