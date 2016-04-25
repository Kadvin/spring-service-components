/**
 * Developer: Kadvin Date: 14-7-4 上午9:14
 */
package net.happyonroad.platform.repository;

import net.happyonroad.spring.service.ServiceExporter;
import net.happyonroad.util.StringAwareTypeHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

import static java.lang.String.format;

/**
 * <h1>关于数据库的配置</h1>
 *
 * 之所以不把这部分工作完全放到 DatabaseAppConfig 中，是因为许多测试程序也都要直接引用之
 */
@Configuration
public class DatabaseConfig implements InitializingBean {
    static Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    //在被测试程序使用时，无需对外注册服务
    @Autowired(required = false)
    ServiceExporter exporter;

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
        return createDataSource();
    }

    public static DataSource createDataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        String dbHost = System.getProperty("db.host", "localhost");
        String dbPort = System.getProperty("db.port", "3306");
        String dbUser = System.getProperty("db.user", "itsnow");
        String dbPass = System.getProperty("db.password", "secret");
        String dbName = System.getProperty("db.name");
        //注意: 必须设置 allowMultiQueries=true，这样 mybatis 才能使用底层的MySQL Driver一次提交多个语句（如insert2张表)
        String dbUrl = format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF8&allowMultiQueries=true",
                              dbHost, dbPort, dbName);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPass);
        logger.info("Connect to mysql {}:{}/{}, identified as {}", dbHost, dbPort, dbName, dbUser);
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        return sqlSessionFactoryBean(dataSource).getObject();
    }


//    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        //故意取名与应用模块的名称不一样，这样保证在测试等情况下，默认的平台配置不会被应用的遮盖
        factory.setConfigLocation(application.getResource("classpath:META-INF/mybatis@platform.xml"));
        factory.setDataSource(dataSource);
        factory.setPlugins(new Interceptor[]{statementInterceptor()});
        return factory;
    }

    @Bean
    public org.apache.ibatis.session.Configuration configuration(SqlSessionFactory sqlSessionFactory){
        return sqlSessionFactory.getConfiguration();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(DataSource dataSource){
        DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
        // 当多个事务方法嵌套在一起执行时，由应用程序决定是否回滚，而不由tx想当然的决定是否回滚
        // 如果不设置这个值，内部方法可预期的exception也会导致外围方法最后回滚
        txManager.setGlobalRollbackOnParticipationFailure(false);
        return txManager;
    }

    @Bean
    public StatementInterceptor statementInterceptor(){
        return new StatementInterceptor();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws PropertyVetoException {
        return new JdbcTemplate(dataSource());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //测试场景 exporter == null
        // 真实场景
        if( exporter == null ) return;
        //数据库模块暴露的服务由数据库模块自行负责
        DataSource dataSource = dataSource();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactory(dataSource);
        exporter.exports(DataSource.class, dataSource);
        exporter.exports(SqlSessionFactory.class, sqlSessionFactory);
//        exporter.exports(SqlSessionFactory.class, sqlSessionFactory);
        exporter.exports(JdbcTemplate.class, jdbcTemplate());
        // 被 activiti 使用
        exporter.exports(PlatformTransactionManager.class, transactionManager(dataSource));
        // 被 mybatis的config模块使用
        exporter.exports(org.apache.ibatis.session.Configuration.class, configuration(sqlSessionFactory));
        TypeHandlerRegistry registry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
        registry.register(Object.class, new StringAwareTypeHandler(registry));
    }
}
