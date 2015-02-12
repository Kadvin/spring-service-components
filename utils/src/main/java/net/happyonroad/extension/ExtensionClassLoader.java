/**
 * Developer: Kadvin Date: 15/2/12 上午9:15
 */
package net.happyonroad.extension;

import net.happyonroad.component.classworld.ManipulateClassLoader;

import java.net.URL;
import java.util.Set;

/**
 * <h1>特定扩展包的ClassLoader</h1>
 * 本加载器是服务于特定扩展包的，其主要可以从该扩展包中加载所有的类
 * 其父ClassLoader为Spring Component Framework的Main Class Loader
 * 其自身继承于URLClassLoader，url仅包括自身组件
 *
 * 另外，为了避免在多个扩展包之间重复加载同一个第三方类，所有扩展包依赖的第三方包均会委托给Main Class Loader加载
 */
public class ExtensionClassLoader extends ManipulateClassLoader{
    final ManipulateClassLoader mcl;

    public ExtensionClassLoader(ManipulateClassLoader parent) {
        super(parent);
        mcl = parent;
    }

    public void addURL(URL url){
        //本组件url自己留下
        super.innerAddURL(url);
    }

    @Override
    public void addURLs(Set<URL> urls) {
        //所有第三方url都提交给parent
        mcl.addURLs(urls);
    }
}
