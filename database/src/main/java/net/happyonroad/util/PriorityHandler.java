/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.type.Priority;
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
@MappedJdbcTypes(JdbcType.TINYINT)
public class PriorityHandler implements TypeHandler<Priority> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Priority parameter, JdbcType jdbcType) throws SQLException {
        if( parameter == null )
            ps.setInt(i, 0);
        else{
            ps.setInt(i, priorityValue(parameter));
        }
    }

    @Override
    public Priority getResult(ResultSet rs, String columnName) throws SQLException {
        int raw = rs.getInt(columnName);
        return priorityByValue(raw);
    }

    @Override
    public Priority getResult(ResultSet rs, int columnIndex) throws SQLException {
        int raw = rs.getInt(columnIndex);
        return priorityByValue(raw);
    }

    @Override
    public Priority getResult(CallableStatement cs, int columnIndex) throws SQLException {
        int raw = cs.getInt(columnIndex);
        return priorityByValue(raw);
    }

    private Priority priorityByValue(int raw) {
        switch (raw){
            case -2: return Priority.VeryLow;
            case -1: return Priority.Low;
            case 1: return Priority.High;
            case 2: return Priority.VeryHigh;
            case 0:
            default: return Priority.Normal;
        }
    }

    private int priorityValue(Priority param){
        return param.getValue();
    }
}
