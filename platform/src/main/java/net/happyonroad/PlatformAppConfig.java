/**
 * Developer: Kadvin Date: 14-7-14 下午4:22
 */
package net.happyonroad;

import net.happyonroad.component.container.ComponentLoader;
import net.happyonroad.extension.ExtensionManager;
import net.happyonroad.extension.GlobalClassLoader;
import net.happyonroad.platform.repository.DatabaseConfig;
import net.happyonroad.platform.resolver.MybatisFeatureResolver;
import net.happyonroad.service.ExtensionContainer;
import net.happyonroad.spring.config.AbstractAppConfig;
import net.happyonroad.spring.event.ComponentLoadedEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
@Import({UtilUserConfig.class, DatabaseConfig.class})
@ComponentScan("net.happyonroad.platform.support")
public class PlatformAppConfig extends AbstractAppConfig implements ApplicationListener<ComponentLoadedEvent> {
    @Autowired
    ComponentLoader componentLoader;

    @Bean
    public GlobalClassLoader containerAwareClassLoader(ExtensionContainer container) {
        // 用 Thread上下文的Class Loader(main class loader)
        //  比 application 的 Class loader(platform class loader)
        // 更为有效，其可以看到除动态加载的类; Extension Aware特性再看到其他
        return new GlobalClassLoader(Thread.currentThread().getContextClassLoader(), container);
    }

    // 用于加载扩展服务模块
    @Bean
    public ExtensionManager pkgManager() {
        return new ExtensionManager();
    }

    @Override
    protected void doExports() {
        super.doExports();
        exports(ExtensionContainer.class);
    }

    @Override
    protected void beforeExports() {
        MybatisFeatureResolver resolver = componentLoader.getFeatureResolver(MybatisFeatureResolver.FEATURE);
        if (resolver != null) {
            resolver.setPlatformApplication(applicationContext);
        } else{
            throw new ApplicationContextException("Can't find mybatis feature resolver!");
        }
    }

    @Override
    public void onApplicationEvent(ComponentLoadedEvent event) {
        // Only listen to platform loaded event
        if (!"platform".equals(event.getSource().getArtifactId())) return;
        // componentContext.setRootContext(applicationContext);
        // 这些beans是被spring security动态注册过来的，只能在本组件加载之后，再向注册表注册
        try {
            UserDetailsService userDetailsService = applicationContext.getBean(UserDetailsService.class);
            AuthenticationProvider authenticationProvider = applicationContext.getBean(AuthenticationProvider.class);
            PersistentTokenRepository persistentTokenRepository =
                    applicationContext.getBean(PersistentTokenRepository.class);

            // Spring Security Registered
            exports(UserDetailsService.class, userDetailsService);
            exports(AuthenticationProvider.class, authenticationProvider);
            exports(PersistentTokenRepository.class, persistentTokenRepository);
        } catch (BeansException e) {
            System.err.println("Can't import beans init by spring security context : " + e.getMessage());
            System.exit(-1);
        }
    }
}
