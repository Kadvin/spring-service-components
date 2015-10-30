/**
 * Developer: Kadvin Date: 14-7-14 下午4:22
 */
package net.happyonroad;

import net.happyonroad.service.ExtensionContainer;
import net.happyonroad.spring.config.AbstractAppConfig;
import net.happyonroad.spring.event.ComponentLoadedEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationListener;
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
 *   |- Web App Config
 *   |   |- JettyServer
 *   |   |    |- AnnotationConfiguration
 *   |   |    |    |- SpringMvcLoader
 *   |   |    |    |    |- SpringSecurityConfig
 *   |   |    |    |    |   |- SpringMvcConfig
 * </pre>
 */
@org.springframework.context.annotation.Configuration
@Import({UtilUserConfig.class, DatabaseUserConfig.class})
@ComponentScan({"net.happyonroad.platform.support", "net.happyonroad.platform.web.filter"})
public class WebAppConfig extends AbstractAppConfig implements ApplicationListener<ComponentLoadedEvent> {

    @Override
    protected void doExports() {
        super.doExports();
        exports(ExtensionContainer.class);
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
