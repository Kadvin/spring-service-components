/**
 * Developer: Kadvin Date: 14-7-14 下午4:22
 */
package net.happyonroad;

import net.happyonroad.component.container.ComponentLoader;
import net.happyonroad.extension.GlobalClassLoader;
import net.happyonroad.platform.repository.DatabaseConfig;
import net.happyonroad.platform.resolver.MybatisFeatureResolver;
import net.happyonroad.spring.config.AbstractAppConfig;
import org.apache.ibatis.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


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
 *   |   |- ExtensionManager
 *   |   |    | - All kinds of service app in repository folder
 * </pre>
 */
@org.springframework.context.annotation.Configuration
@Import({UtilUserConfig.class, DatabaseConfig.class})
@ComponentScan("net.happyonroad.platform.support")
public class DatabaseAppConfig extends AbstractAppConfig {
    @Autowired
    ComponentLoader componentLoader;

    @Autowired
    GlobalClassLoader classLoader;

    @Override
    protected void beforeExports() {
        Resources.setDefaultClassLoader(classLoader);
        MybatisFeatureResolver resolver = componentLoader.getFeatureResolver(MybatisFeatureResolver.FEATURE);
        if (resolver != null) {
            resolver.setPlatformApplication(applicationContext);
        } else {
            throw new ApplicationContextException("Can't find mybatis feature resolver!");
        }
    }

}
