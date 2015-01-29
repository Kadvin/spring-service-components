/**
 * Developer: Kadvin Date: 14/12/30 下午8:15
 */
package net.happyonroad.service;

import net.happyonroad.component.core.Component;

import java.util.List;

/**
 * Extension的容器
 */
public interface ExtensionContainer {
    List<Component> getExtensions();
}
