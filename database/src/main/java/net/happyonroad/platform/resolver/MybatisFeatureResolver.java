/**
 * Developer: Kadvin Date: 14-7-14 下午5:01
 */
package net.happyonroad.platform.resolver;

import net.happyonroad.component.container.RepositoryScanner;
import net.happyonroad.component.container.feature.AbstractFeatureResolver;
import net.happyonroad.component.core.Component;
import net.happyonroad.component.core.support.DefaultComponent;
import net.happyonroad.util.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.*;

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
    public static final String FEATURE       = "mybatis";
    public static final String DB_CONFIG     = "DB-Config";
    public static final String DB_REPOSITORY = "DB-Repository";
    public static final String DB_MAPPERS    = "DB-Mappers";

    static {
        DefaultComponent.MANIFEST_ATTRS.add(DB_CONFIG);
        DefaultComponent.MANIFEST_ATTRS.add(DB_REPOSITORY);
    }

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
        if (dbRepository == null && readComponentDefaultConfig(component, "D").contains("D")) {
            dbRepository = System.getProperty("default.db.repository", "com.itsnow.*.repository");
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
        if (StringUtils.isBlank(dbConfig)) dbConfig = "classpath?:META-INF/mybatis.xml";
        configureMybatisXml(platformApplication, component, dbConfig);

        RepositoryScanner scanner = new MybatisRepositoryScanner(component, platformApplication, dbRepository);
        component.registerScanner(scanner);

        logger.info("The {} is resolved for mybatis feature", component);
    }

    void configureMybatisXml(ApplicationContext platformApplication, Component component, String dbConfig)
            throws IOException {
        // 剔除 classpath?*:
        if (dbConfig.contains(":"))
            dbConfig = dbConfig.substring(dbConfig.indexOf(':') + 1);

        Resource[] resources = component.getResource().getLocalResourcesUnder(dbConfig);
        for (Resource resource : resources) {
            if (resource == null || !resource.exists()) return;
            logger.info("Found extra mybatis config in {}", resource);
            MybatisRepositoryScanner.configByResource(platformApplication, resource);
        }

    }

    @Override
    public Object release(Component component) {
        Configuration configuration ;
        try {
            configuration = platformApplication.getBean(Configuration.class);
        } catch (IllegalStateException e) {
            return null;
        }
        String raw = component.getManifestAttribute(DB_MAPPERS);
        String[] mappers = StringUtils.isBlank(raw) ? new String[0] : raw.split(";");
        Arrays.sort(mappers);
        //第一步，清理所有该组件加载的known mappers
        unloadKnownMappers(configuration, mappers);
        //第二步，清理 loaded mappers
        unloadLoadedResources(configuration, mappers);
        //第三步，清理 statements
        unloadStatements(configuration, mappers);
        //第四步，清理 result map
        unloadResultMaps(configuration, mappers);
        //第五步，清理 key generators
        unloadKeyGenerators(configuration, mappers);
        //第六步，清理 sql fragments
        unloadSqlFragments(configuration, mappers);
        //由于当前的动态数据库扩展包，没有做alias，handler等额外的高级特性，所以，暂时不进行清理
        return super.release(component);
    }

    @SuppressWarnings("unchecked")
    void unloadKnownMappers(Configuration configuration, String... mappers) {
        Collection<Class<?>> klasses = configuration.getMapperRegistry().getMappers();
        List<Class> removing = new LinkedList<Class>();
        for (Class<?> mapper : klasses) {
            //貌似还有combined class loader的情况
            if (Arrays.binarySearch(mappers, mapper.getName()) >= 0) {
                removing.add(mapper);
            }
        }
        Map knownMappers = steal(configuration.getMapperRegistry(), "knownMappers");
        for (Class key : removing) {
            knownMappers.remove(key);
            logger.debug("Unloaded mapper {}", key);
        }
    }

    void unloadLoadedResources(Configuration configuration, String... mappers) {
        Set<String> loadedResources = steal(configuration, "loadedResources");
        List<String> removing = new LinkedList<String>();
        for (String mapper : mappers) {
            removing.add("interface " + mapper);
            removing.add(mapper.replaceAll("\\.", "/") + ".xml");
        }
        for (String key : removing) {
            loadedResources.remove(key);
            logger.debug("Unloaded resource {}", key);
        }
    }

    void unloadStatements(Configuration configuration, String... mappers) {
        Map<String, ?> statements = steal(configuration, "mappedStatements");
        unloadMap(statements, "statement", mappers);
    }

    void unloadResultMaps(Configuration configuration, String... mappers) {
        Map<String,?> resultMaps = steal(configuration, "resultMaps");
        unloadMap(resultMaps, "result map", mappers);
    }

    void unloadSqlFragments(Configuration configuration, String... mappers) {
        Map<String,?> sqlFragments = steal(configuration, "sqlFragments");
        unloadMap(sqlFragments, "sql fragment", mappers);
    }

    void unloadKeyGenerators(Configuration configuration, String... mappers) {
        Map<String,?> keyGenerators = steal(configuration, "keyGenerators");
        unloadMap(keyGenerators, "key generator", mappers);
    }

    public void setPlatformApplication(ApplicationContext platformApplication) {
        this.platformApplication = platformApplication;
    }

    private static  <T> T steal(Object bean, String property) {
        try {
            //noinspection unchecked
            return (T) FieldUtils.readField(bean, property, true);
        } catch (Exception ex) {
            throw new UnsupportedOperationException("Can't steal " + property + " from " + bean, ex);
        }
    }

    private void unloadMap(Map<String,?> map, String type, String... mappers){
        List<String> removing = new LinkedList<String>();
        for (String key : map.keySet()) {
            for (String mapper : mappers) {
                if( key.startsWith(mapper)){
                    removing.add(key);
                    String suffix = StringUtils.substringAfter(key, mapper + ".");
                    if (StringUtils.isNotBlank(suffix)) {
                        removing.add(suffix);
                    }
                }
            }
        }
        for (String key : removing) {
            try {
                map.get(key);
                map.remove(key);
                logger.debug("Unloaded {} {}", type, key);
            } catch (IllegalArgumentException e) {
                //ignore it, because of Ambiguity
            }
        }
    }
}
