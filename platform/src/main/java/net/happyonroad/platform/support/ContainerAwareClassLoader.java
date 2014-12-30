/**
 * Developer: Kadvin Date: 14/12/30 下午8:18
 */
package net.happyonroad.platform.support;

import net.happyonroad.component.core.Component;
import net.happyonroad.platform.service.ServicePackageContainer;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h1>能够从所有的组件中加载类的加载器</h1>
 * <p/>
 * Web应用需要根据类的名称反射类对象，如果类是由服务包定义的，Jetty/WebServer
 * 默认的类加载器(platform class loader)无法加载到这些类
 * 即便是总体的类加载器(release class loader)也无法加载到这些类
 */
public class ContainerAwareClassLoader extends ClassLoader {
    private final ServicePackageContainer  container;
    //按照依赖关系倒序排列
    //  依赖别人最多的最先被检查
    private       List<ApplicationContext> applications;

    public ContainerAwareClassLoader(ClassLoader parent, ServicePackageContainer container) {
        super(parent);
        this.container = container;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // find in parent class loader first
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
            //skip

            for (ApplicationContext application : getApplications()) {
                try {
                    return application.getClassLoader().loadClass(name);
                } catch (ClassNotFoundException ex) {
                    //try next
                }
            }
            throw new ClassNotFoundException(name);
        }
    }

    protected List<ApplicationContext> getApplications() {
        if (applications == null || applications.isEmpty()) {

            List<Component> components = container.getServicePackages();
            applications = new ArrayList<ApplicationContext>(components.size());
            for (Component component : components) {
                applications.add(component.getApplication());
            }
            Collections.reverse(applications);
        }
        return applications;
    }
}
