/**
 * Developer: Kadvin Date: 14-7-11 下午3:49
 */
package net.happyonroad.platform.config;

import net.happyonroad.messaging.MessageBus;
import net.happyonroad.cache.CacheService;
//import AutoNumberService;
import net.happyonroad.spring.service.AbstractServiceConfig;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 缺省的平台扩展应用服务配置（定义默认导入哪些服务）
 *
 */
public class DefaultServiceConfig extends AbstractServiceConfig {

    @Override
    public void defineServices() {
        //数据库相关服务
        importService(DataSource.class);
        importService(PlatformTransactionManager.class, "*", "transactionManager");
        importService(SqlSessionFactory.class);
        importService(Configuration.class);
        importService(JdbcTemplate.class);
        //一般工具服务
        //importService(AutoNumberService.class);

        //redis,message-bus,cache-service
        importService(CacheService.class,System.getProperty("messaging.provider"), "globalCacheService");
        importService(MessageBus.class,System.getProperty("cache.provider"), "globalMessageBus");
    }
}
