package net.happyonroad.menu.model;

import net.happyonroad.menu.model.MenuItem;
import net.happyonroad.support.JsonSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.sql.Timestamp;
import java.util.Set;

/**
 * <h1>MenuItem测试类</h1>
 */
public class MenuItemTest {

    private static ValidatorFactory factory   = Validation.buildDefaultValidatorFactory();
    private static Validator        validator = factory.getValidator();

    private MenuItem menuItem;

    @Before
    public void setUp() throws Exception {
        menuItem = new MenuItem();
        this.menuItem.setId(1L);
        this.menuItem.setName("用户");
        this.menuItem.setState("index.user");
        this.menuItem.setPosition(0L);
        this.menuItem.setShortcut("Shift+Ctrl+A");
        this.menuItem.setDescription("This is a test.");
        this.menuItem.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        this.menuItem.setUpdatedAt(this.menuItem.getCreatedAt());
    }

    @Test
    public void testEmptyMenuItemIsNotValid() throws Exception {
        menuItem.setName(null);
        menuItem.setState(null);
        Set<ConstraintViolation<MenuItem>> violations = validator.validate(menuItem);
        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void testCorrectMenuItemIsValid() throws Exception {
        menuItem.setName("用户");
        menuItem.setState("index.user");
        Set<ConstraintViolation<MenuItem>> violations = validator.validate(menuItem);
        Assert.assertTrue(violations == null || violations.isEmpty());
    }

    @Test
    public void testJson() throws Exception {
        String json = JsonSupport.toJSONString(menuItem);
        System.out.println(json);
        Assert.assertNotNull(json);
    }

}
