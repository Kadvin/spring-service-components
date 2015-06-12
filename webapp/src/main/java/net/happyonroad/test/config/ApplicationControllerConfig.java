/**
 * xiongjie on 14-8-7.
 */
package net.happyonroad.test.config;

import net.happyonroad.platform.web.interceptor.AfterFilterInterceptor;
import net.happyonroad.platform.web.interceptor.BeforeFilterInterceptor;
import net.happyonroad.platform.web.support.PageRequestResponseBodyMethodProcessor;
import org.apache.commons.lang.reflect.FieldUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;

/**
 * <h1>支持对ApplicationController的子类进行测试</h1>
 *
 * 主要是将 平台设定的 @BeforeFilter, @AfterFilter机制注入到 Web App Context 中
 */
public class ApplicationControllerConfig extends WebMvcConfigurationSupport implements InitializingBean {
    // interceptors
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        RequestMappingHandlerAdapter adapter = requestMappingHandlerAdapter();
        registry.addInterceptor(new BeforeFilterInterceptor(adapter));
        registry.addInterceptor(new AfterFilterInterceptor(adapter));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RequestMappingHandlerAdapter adapter = this.requestMappingHandlerAdapter();
        //hacking the adapter returnValueHandlers
        // replace RequestResponseBodyMethodProcessor with Delayed RequestResponseBodyMethodProcessor
        // property path: adapter#returnValueHandlers#returnValueHandlers
        Object composite = FieldUtils.readField(adapter, "returnValueHandlers", true);
        //noinspection unchecked
        List<HandlerMethodReturnValueHandler> actual = (List<HandlerMethodReturnValueHandler>)FieldUtils.readField( composite, "returnValueHandlers", true);
        int index = -1;
        for (int i = 0; i < actual.size(); i++) {
            Object o = actual.get(i);
            if( o instanceof RequestResponseBodyMethodProcessor){
                index = i; break;
            }
        }
        if( index == -1 )
            throw new IllegalStateException("Can't find any RequestResponseBodyMethodProcessor in requestMappingHandlerAdapter#handlers");
        PageRequestResponseBodyMethodProcessor pageRenderer = new PageRequestResponseBodyMethodProcessor(getMessageConverters(), mvcContentNegotiationManager());
        actual.add(index, pageRenderer);
        System.out.println("Insert Page RequestResponseBodyMethodProcessor before origin");

    }

    @Override
    protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        System.out.println("Add Return value handlers");
    }
}
