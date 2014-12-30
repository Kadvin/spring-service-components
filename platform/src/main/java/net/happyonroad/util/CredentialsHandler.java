/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.model.Credential;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Credentials Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CredentialsHandler implements TypeHandler<Credential[]> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Credential[] parameter, JdbcType jdbcType) throws SQLException {
        if( parameter == null )
            ps.setString(i, null);
        else{
            ps.setString(i, ParseUtils.toJSONString(parameter));
        }
    }

    @Override
    public Credential[] getResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToCredentials(raw);
    }

    @Override
    public Credential[] getResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToCredentials(raw);
    }

    @Override
    public Credential[] getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToCredentials(raw);
    }

    private Credential[] stringToCredentials(String raw) {
        if( raw != null ){
            return ParseUtils.parseJson(raw, Credential[].class);
        }
        return null;
    }
}
