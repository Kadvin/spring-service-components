/**
 * Developer: Kadvin Date: 15/2/2 下午6:57
 */
package net.happyonroad;

import net.happyonroad.cache.CacheService;
import net.happyonroad.component.container.ServiceImporter;
import net.happyonroad.messaging.MessageBus;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PlatformUserConfig {
    @Autowired
    ServiceImporter importer;

    @Bean
    DataSource dataSource(){
        return importer.imports(DataSource.class);
    }

    @Bean
    PlatformTransactionManager transactionManager(){
        return importer.imports(PlatformTransactionManager.class);
    }

    @Bean
    SqlSessionFactory sqlSessionFactory(){
        return importer.imports(SqlSessionFactory.class);
    }

    @Bean
    JdbcTemplate jdbcTemplate(){
        return importer.imports(JdbcTemplate.class);
    }

    @Bean
    org.apache.ibatis.session.Configuration dbConfiguration(){
        return importer.imports(org.apache.ibatis.session.Configuration.class);
    }

    @Bean
    CacheService cacheService(){
        return importer.imports(CacheService.class, System.getProperty("cache.provider", "default"));
    }
    @Bean
    MessageBus messageBus(){
        return importer.imports(MessageBus.class, System.getProperty("messaging.provider", "default"));
    }

}
