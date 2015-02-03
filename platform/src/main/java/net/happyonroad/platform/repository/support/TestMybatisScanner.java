/**
 * Developer: Kadvin Date: 15/2/2 下午8:55
 */
package net.happyonroad.platform.repository.support;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * <h1>另外一种是单元测试使用，其中component对象为空</h1>
 */
public class TestMybatisScanner extends MybatisRepositoryScanner {
    public TestMybatisScanner(ApplicationContext platformContext) {
        super(platformContext);
    }


    @Override
    protected BeanDefinitionRegistry createBeanRegistry() {
        return (BeanDefinitionRegistry) applicationContext;
    }

    @Override
    protected ResourcePatternResolver getResourceLoader() {
        return applicationContext;
    }
}
