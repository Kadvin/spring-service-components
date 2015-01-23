/**
 * Developer: Kadvin Date: 14-7-14 下午5:15
 */
package net.happyonroad.platform;

import net.happyonroad.platform.web.handler.DelegateWebSocketHandler;
import net.happyonroad.spring.service.AbstractServiceConfig;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 平台对各个扩展模块的服务配置（主要是暴露哪些服务出去）
 */
public class ServiceConfiguration extends AbstractServiceConfig {
    @Override
    public void defineServices() {
        //数据库相关服务
        exportService(DataSource.class);
        exportService(SqlSessionFactory.class);
        exportService(JdbcTemplate.class);
        // 被 activiti 使用
        exportService(PlatformTransactionManager.class);
        // 被 mybatis的config模块使用
        exportService(Configuration.class);

        // Spring Security
        exportService(UserDetailsService.class, "default");
        exportService(AuthenticationProvider.class, "default");
        exportService(PersistentTokenRepository.class, "default");

        //Spring North WebSocket Handler
        exportService(DelegateWebSocketHandler.class, "north", "northWebSocketHandler");
        //Spring South WebSocket Handler
        exportService(DelegateWebSocketHandler.class, "south", "southWebSocketHandler");
  }
}
