/**
 * Developer: Kadvin Date: 14/12/22 下午8:59
 */
package net.happyonroad.util;

import net.happyonroad.model.Credential;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

/**
 * Credentials Mybatis Handler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CredentialsHandler extends GenericJsonHandler<Credential[]> {
    @Override
    protected Class<Credential[]> objectClass() {
        return Credential[].class;
    }
}
