/**
 * Developer: Kadvin Date: 15/2/2 下午8:55
 */
package net.happyonroad.platform.repository.support;

import net.happyonroad.component.core.Component;
import net.happyonroad.spring.context.ContextUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * <h1>一种是实际使用，其中component对象不为空</h1>
 */
public class ProductMybatisScanner extends MybatisRepositoryScanner {
    final Component component;
    final PathMatchingResourcePatternResolver resolver;
    GenericApplicationContext repositoryContext;

    public ProductMybatisScanner(ApplicationContext platformContext, Component component) {
        super(platformContext);
        this.component = component;
        this.resolver = new PathMatchingResourcePatternResolver(component.getClassLoader());
    }
    @Override
    protected DefaultListableBeanFactory createBeanRegistry() {
        //这个app context 可以访问到框架的context，并存储mybatis搜索到的DAO对象
        repositoryContext = new GenericApplicationContext(applicationContext);
        repositoryContext.setDisplayName("Repository Context for [" + component.getDisplayName() + "]");
        ContextUtils.inheritParentProperties(applicationContext, repositoryContext);
        repositoryContext.setClassLoader(component.getClassLoader());
        repositoryContext.refresh();
        return repositoryContext.getDefaultListableBeanFactory();
    }

    @Override
    protected ResourcePatternResolver getResourceLoader() {
        return resolver;
    }

    @Override
    public int scan(String... packages) {
        int count = super.scan(packages);
        if( count > 0 && repositoryContext != null ){
            // 将存储扫描结果的app context作为 component的parent context属性
            // component的application context将会以此作为其parent
            // 这是框架层留下的扩展机制
            component.setParentContext(repositoryContext);
        }
        return count;
    }
}
