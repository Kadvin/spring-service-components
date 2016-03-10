/**
 * Developer: Kadvin Date: 14/12/30 下午8:15
 */
package net.happyonroad.service;

import net.happyonroad.component.core.Component;
import net.happyonroad.exception.ExtensionException;
import net.happyonroad.extension.ExtensionClassLoader;

import java.io.File;
import java.util.List;
import java.util.Observer;

/**
 * Extension的容器
 */
public interface ExtensionContainer {

    /**
     * <h2>获取所有已经加载的扩展的组件对象</h2>
     *
     * @return 所有已经加载的扩展的组件对象
     */
    List<Component> getExtensions();

    /**
     * <h2>获取所有已经加载的扩展的Class Loaders</h2>
     *
     * @return 所有已经加载的扩展的Class Loaders
     */
    List<ExtensionClassLoader> getExtensionClassLoaders();

    /**
     * <h2>加载一批指定文件中的扩展包</h2>
     *
     * @param files 待加载的扩展包
     */
    Component[] loadExtensions(File[] files) throws ExtensionException;

    /**
     * <h2>解析指定文件中的扩展包为组件(并不加载)</h2>
     *
     * @param file 扩展包
     */
    Component resolve(File file) throws ExtensionException;

    /**
     * <h2>加载指定文件中的扩展包</h2>
     *
     * @param file 扩展包
     */
    Component loadExtension(File file) throws ExtensionException;

    /**
     * <h2>卸载指定文件对应的扩展包</h2>
     *
     * @param file 扩展包
     */
    void unloadExtension(File file) throws ExtensionException;

    /**
     * <h2>判断某个组件是否已经被成功加载</h2>
     */
    boolean isLoaded(String componentId);

    /**
     * <h2> Add a observer </h2>
     * @param observer observer the extension changed by lightweight events
     */
    void addObserver(Observer observer);
}
