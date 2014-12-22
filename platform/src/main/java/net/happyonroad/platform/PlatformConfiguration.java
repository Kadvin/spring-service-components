/**
 * Developer: Kadvin Date: 14-7-14 下午4:22
 */
package net.happyonroad.platform;

import net.happyonroad.platform.repository.DatabaseConfig;
import net.happyonroad.platform.service.AutoNumberService;
import net.happyonroad.platform.services.ServicePackageManager;
import net.happyonroad.platform.support.AutoNumberInMemory;
import net.happyonroad.platform.support.JettyServer;
import net.happyonroad.platform.support.PlatformEventForwarder;
import net.happyonroad.spring.config.DefaultAppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


/**
 * 平台启动配置
 * <h2>被Spring Component Framework加载的应用配置</h2>
 *
 * 加载的逻辑顺序为:
 * <pre>
 * Spring Component AppLauncher
 *   |- Platform Configuration
 *   |   |- Config module
 *   |   |- Database module
 *   |   |- JettyServer
 *   |   |    |- AnnotationConfiguration
 *   |   |    |    |- SpringMvcLoader
 *   |   |    |    |    |- SpringSecurityConfig
 *   |   |    |    |    |   |- SpringMvcConfig
 *   |   |- ServicePackageManager
 *   |   |    | - All kinds of service app in repository folder
 * </pre>
 */
@org.springframework.context.annotation.Configuration
@Import({DefaultAppConfig.class, DatabaseConfig.class})
public class PlatformConfiguration {

    // 用于启动WEB应用
    @Bean
    public JettyServer jettyServer(){
        return new JettyServer();
    }

    // 用于加载扩展服务模块
    @Bean
    public ServicePackageManager pkgManager(){
        return new ServicePackageManager();
    }

    //用于把平台context中的事件转发给 躲在dispatcher servlet 中的 Spring Mvc Context
    @Bean
    public PlatformEventForwarder eventForwarder(){
        return new PlatformEventForwarder();
    }


    @Bean
    public AutoNumberService autoNumberService(){
        return new AutoNumberInMemory();
    }
}
