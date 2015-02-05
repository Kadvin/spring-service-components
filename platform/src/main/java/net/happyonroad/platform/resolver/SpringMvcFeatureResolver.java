/**
 * Developer: Kadvin Date: 14-7-14 下午5:01
 */
package net.happyonroad.platform.resolver;

import net.happyonroad.component.container.feature.AbstractFeatureResolver;
import net.happyonroad.component.container.feature.ApplicationFeatureResolver;
import net.happyonroad.component.core.Component;
import net.happyonroad.util.StringUtils;

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
        //35： Spring Application Context之前(30)，加载
        //65:  Spring Application Context(70) 之前卸载
        // 其实没有做任何卸载动作，卸载顺序无所谓
        super(25, 65);
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
            webRepository = System.getProperty("default.web.repository", "dnt.*.web.controller");
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
                //将 db repository 作为 app repository的一段，让application feature resolver scan before refresh
        String appRepository = component.getManifestAttribute(ApplicationFeatureResolver.APP_REPOSITORY);
        if( appRepository == null ) appRepository = "";
        appRepository = webRepository + ";" + appRepository;
        component.setManifestAttribute(ApplicationFeatureResolver.APP_REPOSITORY, appRepository);
        logger.info("The {} is resolved for spring mvc feature", component);
    }

}
