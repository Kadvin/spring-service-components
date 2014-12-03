/**
 * xiongjie on 14-8-7.
 */
package net.happyonroad.test.controller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * <h1>支持对Application Controller的子类进行测试</h1>
 *
 * 主要是:
 * <ul>
 * <li>设定runner， spring env profile
 * <li>构建mock mvc
 * <li>提供request/response decoration
 * </ul>
 */
@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class ApplicationControllerTest {
    @Autowired
    protected WebApplicationContext wac;
    protected MockMvc browser;

    @Before
    public void setupApplicationController() {
        this.browser = MockMvcBuilders.webAppContextSetup(this.wac).build();
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
