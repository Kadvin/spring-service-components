/**
 * xiongjie on 14-8-6.
 */
package net.happyonroad.platform.resolver;

import net.happyonroad.component.container.RepositoryScanner;
import net.happyonroad.component.core.Component;
import net.happyonroad.platform.util.BeanFilter;
import net.happyonroad.spring.Bean;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LongTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * <h1>对mybatis的scanner进行filter</h1>
 * <p/>
 * 支持两种使用方式
 */
public class MybatisRepositoryScanner extends Bean implements RepositoryScanner {

    final Component          component;
    final String[]           packages;
    ApplicationContext applicationContext;

    SqlSessionFactory sqlSessionFactory;
    BeanFilter        filter;

    public MybatisRepositoryScanner(Component component, ApplicationContext context, String... packages) {
        this.component = component;
        this.applicationContext = context;
        this.packages = packages;
        this.sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        Configuration configuration = applicationContext.getBean(Configuration.class);
        initConfiguration(configuration);
    }

    void initConfiguration(Configuration configuration) {
        //Record的子类/Long类型
        LongTypeHandler handler = new LongTypeHandler();
        configuration.getTypeHandlerRegistry().register(Serializable.class, JdbcType.BIGINT, handler);
        configuration.getTypeHandlerRegistry().register(Serializable.class, null, handler);
    }

    @Override
    public void bind(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override
    public void configure(String config) throws Exception {
        Resource[] resources = getResourceLoader().getResources(config);
        for (Resource resource : resources) {
            if (resource == null || !resource.exists()) return;
            configByResource(applicationContext, resource);
        }
    }

    protected BeanDefinitionRegistry createBeanRegistry() {
        return (BeanDefinitionRegistry) applicationContext;
    }

    protected ResourcePatternResolver getResourceLoader() {
        return applicationContext;
    }

    @Override
    public int scan(String... packages) {
        List<String> targets = new ArrayList<String>();
        targets.addAll(Arrays.asList(this.packages));
        targets.addAll(Arrays.asList(packages));

        BeanDefinitionRegistry beanRegistry = createBeanRegistry();
        ClassPathMapperScanner scanner = new MybatisClassPathMapperScanner(beanRegistry);
        scanner.setSqlSessionFactory(sqlSessionFactory);
        scanner.setResourceLoader(getResourceLoader());
        scanner.setIncludeAnnotationConfig(false);
        scanner.registerFilters();
        int count = scanner.scan(targets.toArray(new String[targets.size()]));
        if (count > 0 && logger.isDebugEnabled()) {
            logger.debug("Scanned {} mybatis repositories", count);

            String[] names = scanner.getRegistry().getBeanDefinitionNames();
            for (String beanName : names) {
                BeanDefinition beanDefinition = scanner.getRegistry().getBeanDefinition(beanName);
                if ((beanDefinition instanceof ScannedGenericBeanDefinition) &&
                    ((ScannedGenericBeanDefinition) beanDefinition).getBeanClass() == MapperFactoryBean.class) {
                    logger.debug("\t{}", beanDefinition.getPropertyValues().get("mapperInterface"));
                }
            }
        }
        return count;
    }

    public void setFilter(BeanFilter filter) {
        this.filter = filter;
    }

    public static void configByResource(ApplicationContext platformApplication, Resource resource) throws IOException {
        Logger logger = LoggerFactory.getLogger(MybatisRepositoryScanner.class);
        InputStream stream = resource.getInputStream();
        try {
            Configuration configuration = platformApplication.getBean(Configuration.class);
            XMLConfigBuilder builder = new XMLConfigBuilder(stream,
                                                            configuration.getEnvironment().toString(),
                                                            configuration.getVariables());
            Configuration extraConfiguration = builder.getConfiguration();
            builder.parse();
            //将新定义的变量设置到原configuration上
            configuration.setVariables(extraConfiguration.getVariables());
            //将新定义的类型别名注册上去
            Map<String, Class<?>> typeAliases = extraConfiguration.getTypeAliasRegistry().getTypeAliases();
            Map<String, Class<?>> existAliases = configuration.getTypeAliasRegistry().getTypeAliases();
            for (Map.Entry<String, Class<?>> entry : typeAliases.entrySet()) {
                if (existAliases.containsKey(entry.getKey())) continue;
                configuration.getTypeAliasRegistry().registerAlias(entry.getKey(), entry.getValue());
                logger.debug("Registered type alias: '{}' -> '{}'", entry.getValue(), entry.getKey());
            }
            //将新定义的 plugin 注册上去
            List<Interceptor> existInterceptors = configuration.getInterceptors();
            for (Interceptor plugin : extraConfiguration.getInterceptors()) {
                if (existInterceptors.contains(plugin)) continue;
                configuration.addInterceptor(plugin);
                logger.debug("Registered plugin: '{}'", plugin);
            }
            // 将新定义的type handler注册上去
            Collection<String> existTypeHandlerNames = new HashSet<String>();
            for (TypeHandler<?> typeHandler : configuration.getTypeHandlerRegistry().getTypeHandlers()) {
                existTypeHandlerNames.add(typeHandler.toString());
            }
            for (TypeHandler<?> typeHandler : extraConfiguration.getTypeHandlerRegistry().getTypeHandlers()) {
                if (existTypeHandlerNames.contains(typeHandler.toString())) continue;
                configuration.getTypeHandlerRegistry().register(typeHandler);
                logger.debug("Registered type handler: '{}'", typeHandler);
            }
            // 暂时限制： ，
            //   Object Factory,
            //   Object Wrapper Factory,
            //   Settings
            //   Environments
            //   Mappers, 额外的config里面不能配置mapper规则 mapper规则必须通过DB-Repository属性配置
            //   databaseIdProvider
            // 等暂时均不可以额外配置
        } finally {
            stream.close();
        }
    }

    class MybatisClassPathMapperScanner extends ClassPathMapperScanner {

        public MybatisClassPathMapperScanner(BeanDefinitionRegistry beanRegistry) {
            super(beanRegistry);
        }

        @Override
        protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition)
                throws IllegalStateException {
            boolean superResult = super.checkCandidate(beanName, beanDefinition);
            boolean accept = filter == null || filter.accept(beanName, beanDefinition);
            boolean result = superResult && accept;
            if (result && component != null) {
                String mapper = mapperOf(beanDefinition);
                String mappers = component.getManifestAttribute(MybatisFeatureResolver.DB_MAPPERS);
                if( mappers == null ){
                    mappers = mapper;
                }else {
                    if( !mappers.contains(mapper)){
                        mappers += ";" + mapper;
                    }
                }
                component.setManifestAttribute(MybatisFeatureResolver.DB_MAPPERS, mappers);
            }
            return result;
        }

        String mapperOf(BeanDefinition beanDefinition) {
            return beanDefinition.getBeanClassName();
        }
    }
}
