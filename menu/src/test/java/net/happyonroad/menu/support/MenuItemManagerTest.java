package net.happyonroad.menu.support;

import net.happyonroad.menu.model.MenuItem;
import net.happyonroad.menu.service.MenuItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;

@ContextConfiguration(classes = MenuItemManagerConfig.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class MenuItemManagerTest {

    @Autowired
    MenuItemService service;

    @Test
    public void testFindById() throws Exception {
        Assert.notNull(service.find(1L));
    }

    @Test
    public void testFindAll() throws Exception {
        List<MenuItem> menuItemList = service.findAll(true);
        System.out.print(menuItemList);
        Assert.notNull(menuItemList);
    }

}
