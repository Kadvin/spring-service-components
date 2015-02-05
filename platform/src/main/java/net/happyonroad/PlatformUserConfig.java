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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * <h1>使用/依赖平台的应用程序的配置</h1>
 *
 * 将会导入平台提供的所有相关服务，典型的是数据库相关服务
 */
@Configuration
//
//这句保证所有 import 本config的app的对象有transaction支持
// 需要transaction支持的类主要包括三种:
//  1. controller: 当transaction从控制器开始时
//  2. service/manager: 当transaction从服务开始时
//  3. repository: 当transaction从DAO开始时
@EnableTransactionManagement
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
