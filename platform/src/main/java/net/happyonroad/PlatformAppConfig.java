/**
 * Developer: Kadvin Date: 14-7-14 下午4:22
 */
package net.happyonroad;

import net.happyonroad.extension.ExtensionAwareClassLoader;
import net.happyonroad.extension.ExtensionManager;
import net.happyonroad.platform.repository.DatabaseConfig;
import net.happyonroad.platform.service.AutoNumberService;
import net.happyonroad.platform.support.AutoNumberInMemory;
import net.happyonroad.platform.support.JettyServer;
import net.happyonroad.platform.support.PlatformEventForwarder;
import net.happyonroad.service.ExtensionContainer;
import net.happyonroad.spring.config.AbstractAppConfig;
import net.happyonroad.spring.config.DefaultAppConfig;
import net.happyonroad.spring.event.ComponentLoadedEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;


/**
 * 平台启动配置
 * <h2>被Spring Component Framework加载的应用配置</h2>
 * <p/>
 * 加载的逻辑顺序为:
 * <pre>
 * Spring Component AppLauncher
 *   |- Platform App Config
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
public class PlatformAppConfig extends AbstractAppConfig implements ApplicationListener<ComponentLoadedEvent> {

    // 用于启动WEB应用
    @Bean
    public JettyServer jettyServer() {
        return new JettyServer();
    }

    @Bean
    public ExtensionAwareClassLoader containerAwareClassLoader(ExtensionContainer container) {
        // 用 Thread上下文的Class Loader(main class loader)
        //  比 application 的 Class loader(platform class loader)
        // 更为有效，其可以看到除动态加载的类; Extension Aware特性再看到其他
        return new ExtensionAwareClassLoader(Thread.currentThread().getContextClassLoader(), container);
    }

    // 用于加载扩展服务模块
    @Bean
    public ExtensionManager pkgManager() {
        return new ExtensionManager();
    }

    //用于把平台context中的事件转发给 躲在dispatcher servlet 中的 Spring Mvc Context
    @Bean
    public PlatformEventForwarder eventForwarder() {
        return new PlatformEventForwarder();
    }


    @Bean
    public AutoNumberService autoNumberService() {
        return new AutoNumberInMemory();
    }

    @Override
    public void onApplicationEvent(ComponentLoadedEvent event) {
        // 这些beans是被spring security动态注册过来的，只能在本组件加载之后，再向注册表注册
        UserDetailsService userDetailsService = applicationContext.getBean(UserDetailsService.class);
        AuthenticationProvider authenticationProvider = applicationContext.getBean(AuthenticationProvider.class);
        PersistentTokenRepository persistentTokenRepository = applicationContext.getBean(PersistentTokenRepository.class);

        // Spring Security Registered
        exports(UserDetailsService.class, userDetailsService);
        exports(AuthenticationProvider.class, authenticationProvider);
        exports(PersistentTokenRepository.class, persistentTokenRepository);
    }
}