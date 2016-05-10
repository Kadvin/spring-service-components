/**
 * xiongjie on 14-8-6.
 */
package net.happyonroad.test.config;

import net.happyonroad.component.container.RepositoryScanner;
import net.happyonroad.platform.resolver.MybatisRepositoryScanner;
import net.happyonroad.platform.util.BeanFilter;
import net.happyonroad.test.support.H2AsMySqlEmbeddedDsConfigurer;
import net.happyonroad.test.support.MigrateResourcePopulator;
import net.happyonroad.util.MiscUtils;
import net.happyonroad.util.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.String.format;

/**
 * <h1>共享的 Repository Test Config</h1>
 *
 * 为了模拟实际运行条件下组件隔离机制
 * 本config类支持Bean Filter机制
 * 开发者需要继承 repositoryFilter() 方法，过滤掉scan出来不需要的 bean 信息
 * 默认为null，不进行过滤
 */
@Profile("test")
public abstract class RepositoryConfig extends AbstractTestExecutionListener
        implements InitializingBean, DisposableBean {
    @Autowired
    ApplicationContext applicationContext;

    boolean destroyed = false;


    @Bean
    public DataSource dataSource(DatabasePopulator populator) throws SQLException {
        if( applicationContext.getEnvironment().acceptsProfiles("mysql")){
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            String dbHost = System.getProperty("db.host", "localhost");
            String dbPort = System.getProperty("db.port", "3306");
            String dbUser = System.getProperty("db.user", "monitor");
            String dbPass = System.getProperty("db.password", "secret");
            String dbName = System.getProperty("db.name", "monitor_test");
            System.setProperty("db.name", dbName);
            //注意: 必须设置 allowMultiQueries=true，这样 mybatis 才能使用底层的MySQL Driver一次提交多个语句（如insert2张表)
            String dbUrl = format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF8&allowMultiQueries=true",
                                  dbHost, dbPort, dbName);
            dataSource.setUrl(dbUrl);
            dataSource.setUsername(dbUser);
            dataSource.setPassword(dbPass);

            // init the database
            Connection connection = dataSource.getConnection();
            try {
                populator.populate(connection);
            } finally {
                connection.close();
            }
            return dataSource;
        }else{
            EmbeddedDatabaseFactory dbFactory = new EmbeddedDatabaseFactory();
            dbFactory.setDatabaseConfigurer(new H2AsMySqlEmbeddedDsConfigurer(dbSchema()));
            dbFactory.setDatabasePopulator(populator);
            return new SingleConnectionDataSource(dbFactory.getDatabase().getConnection(), true);
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        super.afterTestClass(testContext);
        try {
            destroy();
        } catch (Exception e) {
            //skip
        }
    }

    @Override
    public void destroy() throws Exception {
        if( destroyed ) return;
        if( applicationContext.getEnvironment().acceptsProfiles("mysql")) {
            MigrateResourcePopulator populator = databasePopulator();
            populator.setContinueOnError(true);
            populator.setIgnoreFailedDrops(true);
            DataSource dataSource = dataSource(populator);
            String[] scripts = downSqlScripts();
            if( scripts.length > 0 ){
                populator.reset();
                for (String script : scripts) {
                    Resource resource;
                    String direction = null;
                    if( script.contains("@") ){
                        direction = StringUtils.substringAfter(script, "@").trim();
                        script = StringUtils.substringBefore(script, "@").trim();
                    }
                    resource = applicationContext.getResource(script);
                    populator.addScript(resource, direction);
                }
            }else{
                populator.reverse();
            }
            Connection connection = dataSource.getConnection();
            try {
                populator.populate(connection);
            } catch (Exception ex){
                System.err.println("Can't reset mysql db, because of: " + MiscUtils.describeException(ex));
            } finally {
                connection.close();
                destroyed = true;
            }
        }
    }

    @Bean
    MigrateResourcePopulator databasePopulator() {
        MigrateResourcePopulator populator = new MigrateResourcePopulator();
        populator.setIgnoreFailedDrops(true);
        for (String script : sqlScripts()) {
            Resource resource;
            String direction = null;
            if( script.contains("@") ){
                direction = StringUtils.substringAfter(script, "@").trim();
                script = StringUtils.substringBefore(script, "@").trim();
            }
            resource = applicationContext.getResource(script);
            populator.addScript(resource, direction);
        }
        return populator;
    }

    protected abstract String[] sqlScripts();

    protected String[] downSqlScripts(){
        return new String[0];//意味着直接根据 sqlScript逆向，否则，用户需要自行指定
    }

    protected String dbSchema(){
      return "PUBLIC";
    }

    protected  String dbRepository() {
        return "cn.happyonroad.*.repository";
    }

    @Bean
    public RepositoryScanner repositoryScanner(){
        BeanFilter repositoryFilter = repositoryFilter();
        MybatisRepositoryScanner scanner = new MybatisRepositoryScanner(null, applicationContext);
        scanner.setFilter(repositoryFilter);
        return scanner;
    }

    public BeanFilter repositoryFilter(){
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RepositoryScanner scanner = repositoryScanner();
        // TODO 现在的 configuration 没有排序
        scanner.configure("classpath*:META-INF/mybatis.xml");
        String[] repositories = dbRepository().split(";");
        int count = scanner.scan(repositories);
        System.out.println("Found " + count + " db repositories");
    }
}
