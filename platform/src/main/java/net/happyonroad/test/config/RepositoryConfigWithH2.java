/**
 * xiongjie on 14-8-6.
 */
package net.happyonroad.test.config;

import net.happyonroad.platform.repository.RepositoryScanner;
import net.happyonroad.platform.repository.support.MybatisRepositoryScanner;
import net.happyonroad.platform.util.BeanFilter;
import net.happyonroad.test.support.H2AsMySqlEmbeddedDsConfigurer;
import net.happyonroad.test.support.MigrateResourcePopulator;
import net.happyonroad.util.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.init.DatabasePopulator;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * <h1>共享的 Repository Test Config</h1>
 *
 * 为了模拟实际运行条件下组件隔离机制
 * 本config类支持Bean Filter机制
 * 开发者需要继承 repositoryFilter() 方法，过滤掉scan出来不需要的 bean 信息
 * 默认为null，不进行过滤
 */
@Profile("test")
public abstract class RepositoryConfigWithH2 implements InitializingBean {
    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public DataSource dataSource(DatabasePopulator populator) throws SQLException {
        EmbeddedDatabaseFactory dbFactory = new EmbeddedDatabaseFactory();
        dbFactory.setDatabaseConfigurer(new H2AsMySqlEmbeddedDsConfigurer(dbSchema()));
        dbFactory.setDatabasePopulator(populator);
        return new SingleConnectionDataSource(dbFactory.getDatabase().getConnection(), true);
    }

    @Bean
    DatabasePopulator databasePopulator() {
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

    protected String dbSchema(){
      return "PUBLIC";
    }

    protected  String dbRepository() {
        return "dnt.itsnow.repository";
    }

    @Bean
    public RepositoryScanner repositoryScanner(){
        BeanFilter repositoryFilter = repositoryFilter();
        MybatisRepositoryScanner scanner = new MybatisRepositoryScanner(applicationContext);
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
        int count = scanner.scan(dbRepository());
        System.out.println("Found " + count + " db repositories");
    }


}
