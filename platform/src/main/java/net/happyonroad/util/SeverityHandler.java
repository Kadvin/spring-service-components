package net.happyonroad.util;

import net.happyonroad.type.Severity;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by brokenq on 15-1-20.
 */
@MappedJdbcTypes(JdbcType.TINYINT)
public class SeverityHandler implements TypeHandler<Severity>{
    @Override
    public void setParameter(PreparedStatement ps, int i, Severity parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null)
            ps.setInt(i, 0);
        else {
            ps.setInt(i, severityValue(parameter));
        }
    }

    @Override
    public Severity getResult(ResultSet rs, String columnName) throws SQLException {
        int raw = rs.getInt(columnName);
        return severityByValue(raw);
    }

    @Override
    public Severity getResult(ResultSet rs, int columnIndex) throws SQLException {
        int raw = rs.getInt(columnIndex);
        return severityByValue(raw);
    }

    @Override
    public Severity getResult(CallableStatement cs, int columnIndex) throws SQLException {
        int raw = cs.getInt(columnIndex);
        return severityByValue(raw);
    }

    private Severity severityByValue(int raw) {
        switch (raw){
            case 0: return Severity.CLEAR;
            case 2: return Severity.WARNING;
            case 3: return Severity.MINOR;
            case 4: return Severity.MAJOR;
            case 5: return Severity.CRITICAL;
            case 1:
            default: return Severity.INDETERMINATE;
        }
    }

    private int severityValue(Severity param){
        return param.getValue();
    }

}
