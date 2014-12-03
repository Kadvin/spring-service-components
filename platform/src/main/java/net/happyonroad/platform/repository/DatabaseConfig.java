/**
 * Developer: Kadvin Date: 14-7-4 上午9:14
 */
package net.happyonroad.platform.repository;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * <h1>关于数据库的配置</h1>
 */
@Configuration
public class DatabaseConfig {
    @Autowired
    ApplicationContext application;

    /**
     * <h2>给定的数据源未指定schema，需要应用程序在SQL执行时自行指定schema</h2>
     *
     * 备注：实际是mybatis根据不同的业务模型指定
     * @return 数据源
     */
    @Bean
    @Profile("production")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        String dbHost = System.getProperty("db.host", "localhost");
        String dbPort = System.getProperty("db.port", "3306");
        String dbUser = System.getProperty("db.user", "itsnow");
        String dbPass = System.getProperty("db.password", "secret");
        String appId = System.getProperty("app.id");
        if(StringUtils.isBlank(appId) || "<undefined>".equalsIgnoreCase(appId)){
            throw new ApplicationContextException("the app id is not defined");
        }

        String dbName = System.getProperty("db.name", "itsnow_" + appId);
        System.setProperty("db.name", dbName);
        String dbUrl = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF8", dbHost, dbPort, dbName);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPass);
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource){
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setConfigLocation(application.getResource("classpath:META-INF/mybatis.xml"));
        factory.setDataSource(dataSource);
        factory.setPlugins(new Interceptor[]{statementInterceptor()});
        return factory;
    }

    @Bean
    public org.apache.ibatis.session.Configuration configuration( SqlSessionFactory sqlSessionFactory){
        return sqlSessionFactory.getConfiguration();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public StatementInterceptor statementInterceptor(){
        return new StatementInterceptor();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws PropertyVetoException {
        return new JdbcTemplate(dataSource());
    }
}
