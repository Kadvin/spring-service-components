package net.happyonroad.menu.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * <h1>测试CommonMenuItemRepository的MyBatis的Mapping配置是否正确</h1>
 */
@ContextConfiguration(classes = MenuItemRepositoryConfig.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class MenuItemRepositoryTest {

    @Autowired
    MenuItemRepository repository;

    @Test
    public void testFindById() throws Exception {
        Assert.notNull(repository.findById(1L));
        Assert.isNull(repository.findById(0L));
    }

    @Test
    public void testFindAll() throws Exception {
        Assert.notNull(repository.findAll());
    }

}
