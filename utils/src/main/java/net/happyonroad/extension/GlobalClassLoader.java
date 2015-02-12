/**
 * Developer: Kadvin Date: 14/12/30 下午8:18
 */
package net.happyonroad.extension;

import net.happyonroad.component.core.Component;
import net.happyonroad.service.ExtensionContainer;

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
public class GlobalClassLoader extends ClassLoader {
    private final ExtensionContainer         container;
    //按照依赖关系倒序排列
    //  依赖别人最多的最先被检查
    private       List<ExtensionClassLoader> ecls;

    public GlobalClassLoader(ClassLoader parent, ExtensionContainer container) {
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

            for (ExtensionClassLoader ecl : ecls()) {
                try {
                    if (ecl == null ) continue;
                    return ecl.loadClass(name);
                } catch (ClassNotFoundException ex) {
                    //try next
                }
            }
            throw e;
        }
    }

    protected List<ExtensionClassLoader> ecls() {
        if (ecls == null ) {
            List<Component> components = container.getExtensions();
            if (!components.isEmpty()) {
                ecls = new ArrayList<ExtensionClassLoader>(components.size());
                for (Component component : components) {
                    ecls.add((ExtensionClassLoader) component.getClassLoader());
                }
                Collections.reverse(ecls);
            }else{
                //noinspection unchecked
                return Collections.EMPTY_LIST;
            }
        }
        return ecls;
    }
}
