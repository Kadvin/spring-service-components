/**
 * Developer: Kadvin Date: 15/2/2 下午6:57
 */
package net.happyonroad;

import net.happyonroad.spring.config.AbstractUserConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * <h1>使用/依赖平台的应用程序的配置</h1>
 *
 * 将会导入平台提供的所有相关服务，典型的是数据库相关服务
 */
@Configuration
public class PlatformUserConfig extends AbstractUserConfig{
    @Bean
    DataSource dataSource(){
        return imports(DataSource.class);
    }

    @Bean
    PlatformTransactionManager transactionManager(){
        return imports(PlatformTransactionManager.class);
    }

    @Bean
    SqlSessionFactory sqlSessionFactory(){
        return imports(SqlSessionFactory.class);
    }

    @Bean
    JdbcTemplate jdbcTemplate(){
        return imports(JdbcTemplate.class);
    }

    @Bean
    org.apache.ibatis.session.Configuration dbConfiguration(){
        return imports(org.apache.ibatis.session.Configuration.class);
    }

}
