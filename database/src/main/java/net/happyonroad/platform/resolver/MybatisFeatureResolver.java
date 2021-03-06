/**
 * Developer: Kadvin Date: 14-7-14 下午5:01
 */
package net.happyonroad.platform.resolver;

import net.happyonroad.component.container.RepositoryScanner;
import net.happyonroad.component.container.feature.AbstractFeatureResolver;
import net.happyonroad.component.core.Component;
import net.happyonroad.util.StringUtils;
import org.apache.ibatis.io.Resources;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * <h2>Itsnow平台上的服务模块扩展包</h2>
 * <p/>
 * 主要扩展内容有：
 * <p/>
 * <ol>
 * <li>DB {Repository | Mapper}类扩展</li>
 * </ol>
 * <p/>
 * 主要扩展原理是：
 * <pre>
 * 在Spring Service Context加载之后，Spring Application Context加载之前
 * 将需要扩展的对象，调用平台对应的服务构建出来，并注入到对应的Service Context里面去
 * 这样，他们就可以被业务包中的beans访问到
 * </pre>
 */
public class MybatisFeatureResolver extends AbstractFeatureResolver {
    public static final String FEATURE   = "mybatis";
    public static final String DB_CONFIG = "DB-Config";
    public static final String DB_REPOSITORY = "DB-Repository";
    private transient ClassLoader legacyClassLoader;
    ApplicationContext platformApplication;

    public MybatisFeatureResolver() {
        //28： 在Spring Service Context之后(25)， Spring Application Context之前(30)，加载
        // 这样可以为相应的context准备好
        //  repository(mybatis mapper)
        //68:  在Spring Service Context(65) 之后，Spring Application Context(70) 之前卸载
        // 其实没有做任何卸载动作，卸载顺序无所谓
        super(20, 68);
    }

    @Override
    public String getName() {
        return FEATURE;
    }

    @Override
    public void applyDefaults(Component component) {
        super.applyDefaults(component);
        String dbRepository = component.getManifestAttribute(DB_REPOSITORY);
        if( dbRepository == null && readComponentDefaultConfig(component, "D").contains("D")){
            dbRepository = System.getProperty("default.db.repository", "dnt.*.repository");
        }
        component.setManifestAttribute(DB_REPOSITORY, dbRepository);
    }

    @Override
    public boolean hasFeature(Component component) {
        //暂时仅根据组件的artifact id判断，也不根据内容判断
        return StringUtils.isNotBlank(component.getManifestAttribute(DB_REPOSITORY));
    }

    @Override
    public void beforeResolve(Component component) {
        legacyClassLoader = Resources.getDefaultClassLoader();
        Resources.setDefaultClassLoader(component.getClassLoader());
    }

    @Override
    public void afterResolve(Component component) {
        Resources.setDefaultClassLoader(legacyClassLoader);
    }

    @Override
    public void resolve(Component component) throws Exception {
        String dbRepository = component.getManifestAttribute(DB_REPOSITORY);
        // 由于本类是平台定义的，所以，其加载cl一定是平台的 class loader
        String dbConfig = component.getManifestAttribute(DB_CONFIG);
        if( StringUtils.isBlank(dbConfig)) dbConfig = "classpath?:META-INF/mybatis.xml";
        configureMybatisXml(platformApplication, component, dbConfig);

        RepositoryScanner scanner = new MybatisRepositoryScanner(platformApplication, dbRepository);
        component.registerScanner(scanner);

        logger.info("The {} is resolved for mybatis feature", component);
    }

    void configureMybatisXml(ApplicationContext platformApplication, Component component, String dbConfig)
            throws IOException {
        // 剔除 classpath?*:
        if( dbConfig.contains(":"))
            dbConfig = dbConfig.substring(dbConfig.indexOf(':') + 1);

        Resource[] resources = component.getResource().getLocalResourcesUnder(dbConfig);
        for (Resource resource : resources) {
            if (resource == null || !resource.exists()) return;
            logger.info("Found extra mybatis config in {}", resource);
            MybatisRepositoryScanner.configByResource(platformApplication, resource);
        }

    }

    public void setPlatformApplication(ApplicationContext platformApplication) {
        this.platformApplication = platformApplication;
    }
}
