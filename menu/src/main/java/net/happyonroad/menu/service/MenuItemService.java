package net.happyonroad.menu.service;

import net.happyonroad.menu.exception.MenuItemException;
import net.happyonroad.menu.model.MenuItem;

import java.util.List;

/**
 * <h1>菜单服务类</h1>
 */
public interface MenuItemService {

    /**
     * <h2>创建菜单</h2>
     *
     * @param parent 父菜单
     * @param menuItem 菜单实体类
     * @return MenuItem
     * @throws net.happyonroad.menu.exception.MenuItemException
     */
    public MenuItem create(MenuItem parent, MenuItem menuItem) throws MenuItemException;

    /**
     * <h2>删除菜单（逻辑删除）</h2>
     * @param id 菜单ID
     * @throws MenuItemException
     */
    public void destroy(Long id) throws MenuItemException;

    /**
     * <h2>修改菜单</h2>
     * @param menuItem 菜单实体类
     * @return MenuItem
     * @throws MenuItemException
     */
    public MenuItem update(MenuItem menuItem) throws MenuItemException;

    /**
     * <h2>查询菜单</h2>
     * @param id the menu id
     * @return MenuItem
     * @throws MenuItemException
     */
    public MenuItem find(Long id) throws MenuItemException;

    /**
     * <h2>加载所有菜单</h2>
     * @return List<MenuItem>
     * @param tree return as tree structure
     */
    public List<MenuItem> findAll(boolean tree);

    /**
     * <h2>根据Path查找菜单项</h2>
     * @param path 菜单的Path
     * @return
     */
    public MenuItem findByPath(String path);

    /**
     * <h2>查找此父菜单下所有的菜单</h2>
     * @param parent 父菜单项
     * @param tree 是否为树结构标识
     * @return
     */
    public List<MenuItem> findAllByParent(MenuItem parent, boolean tree);
}
