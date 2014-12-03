/**
 * xiongjie on 14-8-7.
 */
package net.happyonroad.platform.util;

import org.springframework.beans.factory.config.BeanDefinition;

/**
 * <h1>为各种 Scanner 过滤 bean definition</h1>
 */
public interface BeanFilter {
    boolean accept(String beanName, BeanDefinition definition);
}
