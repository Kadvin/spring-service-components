package net.happyonroad.util;

import net.happyonroad.type.AckStatus;
import net.happyonroad.type.AckStatus;
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
public class AckStatusHandler implements TypeHandler<AckStatus>{
    @Override
    public void setParameter(PreparedStatement ps, int i, AckStatus parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null)
            ps.setInt(i, 0);
        else {
            ps.setInt(i, AckStatusValue(parameter));
        }
    }

    @Override
    public AckStatus getResult(ResultSet rs, String columnName) throws SQLException {
        int raw = rs.getInt(columnName);
        return AckStatusByValue(raw);
    }

    @Override
    public AckStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        int raw = rs.getInt(columnIndex);
        return AckStatusByValue(raw);
    }

    @Override
    public AckStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        int raw = cs.getInt(columnIndex);
        return AckStatusByValue(raw);
    }

    private AckStatus AckStatusByValue(int raw) {
        switch (raw){
            case 0: return AckStatus.Unacked;
            case 2: return AckStatus.Cleared;
            case 1:
            default: return AckStatus.Acked;
        }
    }

    private int AckStatusValue(AckStatus param){
        return param.getValue();
    }

}
