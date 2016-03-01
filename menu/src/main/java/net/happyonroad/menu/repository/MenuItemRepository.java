package net.happyonroad.menu.repository;

import net.happyonroad.menu.model.MenuItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <h1>菜单项持久层</h1>
 */
public interface MenuItemRepository {

    public void create(MenuItem menuItem);

    public void delete(@Param("id") Long id);

    public void update(MenuItem menuItem);

    public MenuItem findById(@Param("id") Long id);

    public List<MenuItem> findAll();

}
