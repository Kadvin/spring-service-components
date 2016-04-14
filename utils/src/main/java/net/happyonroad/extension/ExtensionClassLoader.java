/**
 * Developer: Kadvin Date: 15/2/12 上午9:15
 */
package net.happyonroad.extension;

import net.happyonroad.component.classworld.ManipulateClassLoader;
import net.happyonroad.component.core.Component;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import sun.misc.URLClassPath;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

/**
 * <h1>特定扩展包的ClassLoader</h1>
 * 本加载器是服务于特定扩展包的，其主要可以从该扩展包中加载所有的类
 * 其父ClassLoader为Spring Component Framework的Main Class Loader
 * 其自身继承于URLClassLoader，url仅包括自身组件
 * <p/>
 * 另外，为了避免在多个扩展包之间重复加载同一个第三方类，所有扩展包依赖的第三方包均会委托给Main Class Loader加载
 */
public class ExtensionClassLoader extends ManipulateClassLoader {
    Component component;

    public ExtensionClassLoader(ManipulateClassLoader parent) {
        this(parent, null);
    }

    ExtensionClassLoader(ManipulateClassLoader parent, Component component) {
        super(parent);
        this.component = component;
    }

    @Override
    public void addURL(URL url) {
        //本组件url自己留下
        super.innerAddURL(url);
    }

    @Override
    public void addURLs(Set<URL> urls) {
        //所有第三方url都提交给parent
        ((ManipulateClassLoader) getParent()).addURLs(urls);
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public void close() throws IOException {
        try {
            //通过hacking的方式，关闭扩展的class loader
            Field field = URLClassLoader.class.getDeclaredField("ucp");
            field.setAccessible(true);
            URLClassPath ucp = (URLClassPath) field.get(this);
            ucp.closeLoaders();
        } catch (Exception e) {
            logger.error("Can't close {}, because of {}", this, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public ExtensionClassLoader derive(ManipulateClassLoader parent, Component component) {
        ExtensionClassLoader derived = new ExtensionClassLoader(parent, component);
        URLClassPath ucp;
        try {
            ucp = (URLClassPath) FieldUtils.readField(this, "ucp", true);
        } catch (Exception ex) {
            throw new IllegalStateException("Can't hacking URLClassLoader#ucp field", ex);
        }
        for (URL url : ucp.getURLs()) {
            derived.addURL(url);
        }
        derived.addURL(component.getURL());
        return derived;
    }

    @Override
    public String toString() {
        return "ExtensionClassLoader(" + component + ')';
    }
}
