package net.happyonroad.util;

import org.apache.ibatis.type.*;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <h1>StringAwareTypeHandler</h1>
 *
 * @author Jay Xiong
 */
@MappedTypes(Object.class)
public class StringAwareTypeHandler extends UnknownTypeHandler {

    public StringAwareTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
        super(typeHandlerRegistry);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
            throws SQLException {
        super.setNonNullParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        if( columnName.startsWith("'") && columnName.endsWith("'") )
            return StringUtils.substringBetween(columnName, "'");
        return super.getNullableResult(rs, columnName);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return super.getNullableResult(rs, columnIndex);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return super.getNullableResult(cs, columnIndex);
    }
}
