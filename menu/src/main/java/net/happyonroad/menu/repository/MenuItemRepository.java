package net.happyonroad.menu.repository;

import net.happyonroad.menu.model.MenuItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <h1>菜单项持久层</h1>
 */
public interface MenuItemRepository {

    @Options(useGeneratedKeys = true,keyColumn = "id")
    @Insert("INSERT INTO menu_items (parent_id, name, type, state, css, position, shortcut, description) VALUES " +
            "(#{parentId}, #{name}, #{type}, #{state}, #{css}, #{position} ,#{shortcut}, #{description})")
    public long create(MenuItem menuItem);

    @Delete("DELETE FROM menu_items WHERE id = #{id}")
    public MenuItem delete(@Param("id") Long id);

    @Update("UPDATE menu_items SET " +
            " parent_id    = #{parentId}, " +
            " name         = #{name}, " +
            " type         = #{type}," +
            " state        = #{state}," +
            " css          = #{css} " +
            " position     = #{position} " +
            " shortcut     = #{shortcut} " +
            " description  = #{description} " +
            " WHERE id     = #{id} ")
    public long update(MenuItem menuItem);

    @Select("SELECT * FROM menu_items WHERE id = #{id}")
    public MenuItem findById(@Param("id") Long id);

    @Select("SELECT * FROM menu_items order by position")
    public List<MenuItem> findAll();

}
