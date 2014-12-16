/**
 * Developer: Kadvin Date: 14-7-14 下午5:01
 */
package net.happyonroad.platform.services;

import net.happyonroad.platform.web.controller.ApplicationController;
import net.happyonroad.util.StringUtils;
import net.happyonroad.component.container.feature.AbstractFeatureResolver;
import net.happyonroad.component.core.Component;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import static org.springframework.context.ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS;

/**
 * <h2>Itsnow平台上的服务模块扩展包</h2>
 *
 * 主要扩展内容有：
 *
 * <ol>
 * <li>Spring Controller/Views等扩展</li>
 * </ol>
 *
 * 主要扩展原理是：
 * <pre>
 * 在Spring Application Context加载之后 扫描指定的目录，加载相应的控制器
 * </pre>
 */
public class SpringMvcFeatureResolver extends AbstractFeatureResolver {
    public static final String FEATURE        = "spring-mvc";
    public static final String WEB_REPOSITORY = "Web-Repository";

    public SpringMvcFeatureResolver() {
        //35： Spring Application Context之后(30)，加载
        //65:  Spring Application Context(70) 之前卸载
        // 其实没有做任何卸载动作，卸载顺序无所谓
        super(35, 65);
    }

    @Override
    public String getName() {
        return FEATURE;
    }

    @Override
    public void applyDefaults(Component component) {
        super.applyDefaults(component);
        String webRepository = component.getManifestAttribute(WEB_REPOSITORY);
        if( webRepository == null && readComponentDefaultConfig(component, "W").contains("W")){
            webRepository = System.getProperty("default.web.repository", "dnt.*.web.repository");
        }
        component.setManifestAttribute(WEB_REPOSITORY, webRepository);
    }

    @Override
    public boolean hasFeature(Component component) {
        //暂时仅根据组件的artifact id判断，也不根据内容判断
        return StringUtils.isNotBlank(component.getManifestAttribute(WEB_REPOSITORY));
    }

    @Override
    public void resolve(Component component) throws Exception {
        String webRepository = component.getManifestAttribute(WEB_REPOSITORY);
        scanSpringControllers(component.getApplication(), webRepository);
        logger.info("The {} is resolved for spring mvc feature", component);
    }

    private void scanSpringControllers(ApplicationContext application, String webRepository) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(
                (BeanDefinitionRegistry) application);
        scanner.setEnvironment(application.getEnvironment());
        scanner.setResourceLoader(application);
        scanner.setIncludeAnnotationConfig(false);
        int count = scanner.scan(StringUtils.split(webRepository, CONFIG_LOCATION_DELIMITERS));
        if(count > 0 && logger.isDebugEnabled()){
            logger.debug("Scanned {} controllers", count);
            String[] names = scanner.getRegistry().getBeanDefinitionNames();
            for (String name : names) {
                BeanDefinition definition = scanner.getRegistry().getBeanDefinition(name);
                if( definition instanceof ScannedGenericBeanDefinition){
                    ScannedGenericBeanDefinition sgbd = (ScannedGenericBeanDefinition) definition;
                    Class<?> beanClass;
                    try {
                        beanClass = sgbd.resolveBeanClass(application.getClassLoader());
                    } catch (ClassNotFoundException e) {
                        continue;
                    }
                    if( beanClass != null && ApplicationController.class.isAssignableFrom(beanClass)){
                        logger.debug("\t{}", beanClass.getName());
                    }
                }
            }
        }
    }
}
