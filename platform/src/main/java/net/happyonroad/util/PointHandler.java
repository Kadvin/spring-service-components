/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeHandler;

import java.awt.*;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Point Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class PointHandler implements TypeHandler<Point> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Point parameter, JdbcType jdbcType) throws SQLException {
        if( parameter == null )
            ps.setString(i, null);
        else{
            ps.setString(i, ParseUtils.toJSONString(parameter));
        }
    }

    @Override
    public Point getResult(ResultSet rs, String columnName) throws SQLException {
        String raw = rs.getString(columnName);
        return stringToPoint(raw);
    }

    @Override
    public Point getResult(ResultSet rs, int columnIndex) throws SQLException {
        String raw = rs.getString(columnIndex);
        return stringToPoint(raw);
    }

    @Override
    public Point getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String raw = cs.getString(columnIndex);
        return stringToPoint(raw);
    }

    private Point stringToPoint(String raw) {
        if( raw != null ){
            return ParseUtils.parseJson(raw, Point.class);
        }
        return null;
    }
}
