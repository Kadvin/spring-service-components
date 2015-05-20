package net.happyonroad.menu.web.controller;

import net.happyonroad.menu.model.MenuItem;
import net.happyonroad.menu.service.MenuItemService;
import net.happyonroad.util.ParseUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = MenuItemsControllerConfig.class)
@Ignore
public class MenuItemsControllerTest {

    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    MenuItemService commonMenuItemService;

    List<MenuItem> menuItemList;

    MenuItem menuItem;
    protected MockMvc browser;

    @Before
    public void setup() {
        this.browser = MockMvcBuilders.webAppContextSetup(this.wac).build();

        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        this.menuItem.setId(1L);
        this.menuItem.setName("user");
        this.menuItem.setState("index.user");
        this.menuItem.setPosition(0L);
        this.menuItem.setShortcut("Shift+Ctrl+A");
        this.menuItem.setDescription("This is a test.");
        this.menuItem.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        this.menuItem.setUpdatedAt(this.menuItem.getCreatedAt());
        menuItemList.add(menuItem);

        reset(commonMenuItemService);
    }

    @Test
    public void testShow() throws Exception {

        expect(commonMenuItemService.findAll(true))
                .andReturn(menuItemList);

        replay(commonMenuItemService);

        // 准备 Mock Request
        MockHttpServletRequestBuilder request = get("/api/menu_items");
        request = decorate(request);

        // 执行
        ResultActions result = this.browser.perform(request);

        // 对业务结果的验证
        decorate(result).andExpect(status().isOk());

    }

//    @Test
//    public void testUpdate() throws Exception {
////        expect(commonMenuItemService.findAll(true)).andReturn(menuItemList);
//
//        expect(commonMenuItemService.update(anyObject(MenuItem.class))).andReturn(menuItem);
//        replay(commonMenuItemService);
//
//        MockHttpServletRequestBuilder request = put("/api/menu_items/SLA").content(requestJson());
//        decorate(request);
//
//        ResultActions result = this.browser.perform(request);
//        decorate(result).andExpect(status().isOk());
//    }

    protected String requestJson(){
        return ParseUtils.toJSONString(menuItem);
    }

    // 每次测试结束之后再验证
    @After
    public void tearDown() throws Exception {
        // 对Mock的Expectations进行验证
        verify(commonMenuItemService);
    }

    protected MockHttpServletRequestBuilder decorate(MockHttpServletRequestBuilder request){
        request.contentType(MediaType.APPLICATION_JSON);
        return request;
    }

    protected ResultActions decorate(ResultActions result) throws Exception{
        result.andDo(MockMvcResultHandlers.print());
        result.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        return result;
    }
}
