/**
 * Developer: Kadvin Date: 14-9-16 上午9:50
 */
package net.happyonroad.util;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.util.Properties;

/**
 * 负责序列化 Properties[]
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class PropertiesArrayHandler extends GenericJsonHandler<Properties[]> {
    @Override
    protected Class<Properties[]> objectClass() {
        return Properties[].class;
    }
}
