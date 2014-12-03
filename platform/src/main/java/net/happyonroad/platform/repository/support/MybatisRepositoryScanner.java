/**
 * xiongjie on 14-8-6.
 */
package net.happyonroad.platform.repository.support;

import net.happyonroad.platform.repository.RepositoryScanner;
import net.happyonroad.platform.util.BeanFilter;
import net.happyonroad.spring.Bean;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * <h1>对mybatis的scanner进行filter</h1>
 */
public class MybatisRepositoryScanner extends Bean implements RepositoryScanner {

    ApplicationContext applicationContext;
    SqlSessionFactory  sqlSessionFactory;
    BeanFilter         filter;

    public MybatisRepositoryScanner(ApplicationContext applicationContext) {
        this(applicationContext, applicationContext.getBean(SqlSessionFactory.class));
    }

    public MybatisRepositoryScanner(ApplicationContext applicationContext, SqlSessionFactory sqlSessionFactory) {
        this.applicationContext = applicationContext;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public void configure(String config) throws Exception {
        Resource[] resources = applicationContext.getResources(config);
        for (Resource resource : resources) {
            if (resource == null || !resource.exists()) return;
            configByResource(resource);
        }
    }

    void configByResource(Resource resource) throws IOException {
        logger.info("Found extra mybatis config in {}", resource);

        InputStream stream = resource.getInputStream();
        try {
            Configuration configuration = applicationContext.getBean(Configuration.class);
            XMLConfigBuilder builder = new XMLConfigBuilder(stream,
                                                            configuration.getEnvironment().toString(),
                                                            configuration.getVariables());
            Configuration extraConfiguration = builder.getConfiguration();
            builder.parse();
            //将新定义的变量设置到原configuration上
            configuration.setVariables(extraConfiguration.getVariables());
            //将新定义的类型别名注册上去
            Map<String,Class<?>> typeAliases = extraConfiguration.getTypeAliasRegistry().getTypeAliases();
            Map<String, Class<?>> existAliases = configuration.getTypeAliasRegistry().getTypeAliases();
            for (Map.Entry<String, Class<?>> entry : typeAliases.entrySet()) {
                if( existAliases.containsKey(entry.getKey()) ) continue;
                configuration.getTypeAliasRegistry().registerAlias(entry.getKey(), entry.getValue());
                logger.debug("Registered type alias: '{}' -> '{}'", entry.getValue(), entry.getKey());
            }
            //将新定义的 plugin 注册上去
            List<Interceptor> existInterceptors = configuration.getInterceptors();
            for(Interceptor plugin : extraConfiguration.getInterceptors()){
                if ( existInterceptors.contains(plugin) ) continue;
                configuration.addInterceptor(plugin);
                logger.debug("Registered plugin: '{}'", plugin);
            }
            // 将新定义的type handler注册上去
            Collection<String> existTypeHandlerNames = new HashSet<String>();
            for (TypeHandler<?> typeHandler : configuration.getTypeHandlerRegistry().getTypeHandlers()) {
                existTypeHandlerNames.add(typeHandler.toString());
            }
            for (TypeHandler<?> typeHandler : extraConfiguration.getTypeHandlerRegistry().getTypeHandlers()) {
                if( existTypeHandlerNames.contains(typeHandler.toString())) continue;
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

    @Override
    public int scan(String... packages) {
        ClassPathMapperScanner scanner = new ClassPathMapperScanner((BeanDefinitionRegistry) applicationContext){
            @Override
            protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
                boolean superResult = super.checkCandidate(beanName, beanDefinition);
                boolean accept = filter == null || filter.accept(beanName, beanDefinition);
                return superResult && accept;
            }
        };
        String sqlSessionFactoryBeanName = "sqlSessionFactory";
        scanner.setSqlSessionFactory(sqlSessionFactory);
        scanner.setSqlSessionFactoryBeanName(sqlSessionFactoryBeanName);
        scanner.setResourceLoader(applicationContext);
        scanner.setIncludeAnnotationConfig(false);
        scanner.registerFilters();
        int count = scanner.scan(packages);
        if( count > 0 && logger.isDebugEnabled()){
            logger.debug("Scanned {} mybatis repositories", count);

            String[] names = scanner.getRegistry().getBeanDefinitionNames();
            for (String beanName : names) {
                BeanDefinition beanDefinition = scanner.getRegistry().getBeanDefinition(beanName);
                if( (beanDefinition instanceof ScannedGenericBeanDefinition) &&
                    ((ScannedGenericBeanDefinition) beanDefinition).getBeanClass() == MapperFactoryBean.class){
                    logger.debug("\t{}", beanDefinition.getPropertyValues().get("mapperInterface") );
                }
            }
        }
        return count;
    }

    public void setFilter(BeanFilter filter) {
        this.filter = filter;
    }
}
