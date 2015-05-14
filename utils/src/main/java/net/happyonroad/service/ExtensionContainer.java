/**
 * Developer: Kadvin Date: 14/12/30 下午8:15
 */
package net.happyonroad.service;

import net.happyonroad.component.core.Component;
import net.happyonroad.exception.ExtensionException;

import java.io.File;
import java.util.List;

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
}
