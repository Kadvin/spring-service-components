package net.happyonroad.menu.support;

import net.happyonroad.menu.exception.MenuItemException;
import net.happyonroad.menu.model.MenuItem;
import net.happyonroad.menu.repository.MenuItemRepository;
import net.happyonroad.menu.service.MenuItemService;
import net.happyonroad.spring.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <h1>菜单服务实现类</h1>
 */
@Service
public class MenuItemManager extends Bean implements MenuItemService {

    @Autowired
    private MenuItemRepository repository;

    @Override
    public MenuItem create(MenuItem parent, MenuItem menuItem) throws MenuItemException {
        logger.info("Creating menu item {}", menuItem);
        if(menuItem == null){
            throw new MenuItemException("Menu item entry can not be empty");
        }
        if( parent != null )
            menuItem.setParentId(parent.getId());
        repository.create(menuItem);
        return menuItem;
    }

    @Override
    public void destroy(Long id) throws MenuItemException {
        logger.warn("Deleting menu item id: {}", id);
        if(id==null){
            throw new MenuItemException("ID of menu item can not be empty");
        }
        repository.delete(id);
    }

    @Override
    public MenuItem update(MenuItem menuItem) throws MenuItemException {
        logger.info("Updating menu item {}", menuItem);
        if(menuItem==null){
            throw new MenuItemException("Menu item entry can not be empty");
        }
        repository.update(menuItem);
        return menuItem;
    }

    @Override
    public MenuItem find(Long id) throws MenuItemException {
        logger.debug("Finding menu item by id: {}", id);
        if(id==null){
            throw new MenuItemException("ID of menu item can not be empty");
        }
        return repository.findById(id);
    }

    @Override
    public List<MenuItem> findAll(boolean tree) {
        logger.debug("Finding all of menu items");
        List<MenuItem> menuItemList = repository.findAll();
        if( tree ) menuItemList = this.buildTree(menuItemList);
        return menuItemList;
    }

    @Override
    public List<MenuItem> findAllByParent(MenuItem parent, boolean tree) {
        List<MenuItem> all = repository.findAll();
        this.buildTree(all);
        // Filter by parent
        Iterator<MenuItem> iterator = all.iterator();
        while (iterator.hasNext()) {
            MenuItem item = iterator.next();
            if( !item.getPath().startsWith(parent.getPath())) iterator.remove();
        }
        if( tree ) {
            // Filter by tree, only kept top parent
            iterator = all.iterator();
            while (iterator.hasNext()) {
                MenuItem item = iterator.next();
                if( item.getParent() != null ) iterator.remove();
            }
        }
        return all;
    }

    @Override
    public MenuItem findByPath(String path) {
        //TODO 现在的算法效率很低
        if(path.endsWith("/")) path = path.substring(0, path.length()-1);
        List<MenuItem> all = repository.findAll();
        this.buildTree(all);
        for (MenuItem item : all) {
            if( item.getPath().equals(path)) return item;
        }
        return null;
    }

    /**
     * <h2>创建菜单树</h2>
     *
     * @param menuItems the menu items
     * @return the top menu items with children
     */
    private List<MenuItem> buildTree(List<MenuItem> menuItems){
        Map<Long, MenuItem> map = new HashMap<Long, MenuItem>();
        for (MenuItem item : menuItems) {
            map.put(item.getId(), item);
        }
        List<MenuItem> topMenuItems = new ArrayList<MenuItem>();
        for(MenuItem menuItem : menuItems){
            if(menuItem.getParentId() != null ){
                MenuItem parent = map.get(menuItem.getParentId());
                menuItem.setParent(parent);
            }else{
                topMenuItems.add(menuItem);
            }
        }
        Collections.sort(topMenuItems);
        return topMenuItems;
    }
}
