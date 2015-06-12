/**
 * xiongjie on 14-8-6.
 */
package net.happyonroad.test.support;

import net.happyonroad.spring.Bean;
import org.springframework.context.ApplicationContextException;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.String.format;

/**
 * <h1>Make H2 works like MySQL</h1>
 */
public class H2AsMySqlEmbeddedDsConfigurer extends Bean implements EmbeddedDatabaseConfigurer {

    private String schema;

    public H2AsMySqlEmbeddedDsConfigurer(String schema){
      this.schema = schema;
    }

    @Override
    public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
        try {
            //noinspection unchecked
            properties.setDriverClass((Class<? extends Driver>) Class.forName("org.h2.Driver"));
            // 不能将 schema = %s 设置为连接字符串的属性，因为它的优先级会高于init指令
            // 所以需要将设置schema放到init语句中
            String url = format("jdbc:h2:mem:%s;INIT=CREATE SCHEMA IF NOT EXISTS %s\\;SET SCHEMA %s;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false;MODE=MySQL",
                                databaseName, this.schema.toUpperCase(), this.schema);
            properties.setUrl(url);
            properties.setUsername("sa");
            properties.setPassword("");
        } catch (ClassNotFoundException e) {
            throw new ApplicationContextException("you should configure maven dependency to h2database, scope = test");
        }
    }

    @Override
    public void shutdown(DataSource dataSource, String databaseName) {
        try {
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            stmt.execute("SHUTDOWN");
        } catch (SQLException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not shutdown embedded database", ex);
            }
        }

    }
}
